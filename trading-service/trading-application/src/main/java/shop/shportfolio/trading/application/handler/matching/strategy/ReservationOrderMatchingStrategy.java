package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
public class ReservationOrderMatchingStrategy implements OrderMatchingStrategy<ReservationOrder> {

    private final TradingDomainService tradingDomainService;
    private final TradingOrderRepositoryPort tradingRepository;
    private final CouponInfoTrackHandler couponInfoTrackHandler;
    private final TradingOrderRedisPort tradingOrderRedisPort;
    private final FeePolicy feePolicy;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepository;

    public ReservationOrderMatchingStrategy(TradingDomainService tradingDomainService,
                                            TradingOrderRepositoryPort tradingRepository,
                                            CouponInfoTrackHandler couponInfoTrackHandler,
                                            TradingOrderRedisPort tradingOrderRedisPort,
                                            FeePolicy feePolicy,
                                            TradingTradeRecordRepositoryPort tradingTradeRecordRepository) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepository = tradingRepository;
        this.couponInfoTrackHandler = couponInfoTrackHandler;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
        this.feePolicy = feePolicy;
        this.tradingTradeRecordRepository = tradingTradeRecordRepository;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public List<TradingRecordedEvent> match(OrderBook orderBook, ReservationOrder reservationOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels;
        if (reservationOrder.isBuyOrder()) {
            counterPriceLevels = orderBook.getSellPriceLevels();
        } else {
            counterPriceLevels = orderBook.getBuyPriceLevels();
        }

        Optional<CouponInfo> couponInfoOptional = couponInfoTrackHandler.trackCouponInfo(reservationOrder.getUserId());
        FeeRate baseFeeRate = feePolicy.calculateFeeRate(reservationOrder.getOrderSide());
        FeeRate finalFeeRate = baseFeeRate;

        if (couponInfoOptional.isPresent()) {
            CouponInfo couponInfo = couponInfoOptional.get();
            if (!couponInfo.getUsageExpiryDate().isExpired()) {
                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
                BigDecimal discountDecimal = discountRatio.divide(BigDecimal.valueOf(100));
                finalFeeRate = baseFeeRate.applyDiscount(discountDecimal);
                log.info("Coupon applied: userId={}, discount={}", reservationOrder.getUserId().getValue(), discountDecimal);
            }
        }

        log.info("Start executing ReservationOrder. OrderId={}, RemainingQty={}",
                reservationOrder.getId().getValue(), reservationOrder.getRemainingQuantity().getValue());

        // 예약주문 만료 체크 (초기)
        if (reservationOrder.isExpired(LocalDateTime.now(ZoneOffset.UTC))) {
            log.info("Reservation order expired: {}", reservationOrder.getId().getValue());
            return trades;
        }

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice priceLevel = entry.getKey();
            PriceLevel counterPriceLevel = entry.getValue();

            if (!tradingDomainService.isPriceMatch(reservationOrder, new OrderPrice(priceLevel.getValue()))) {
                continue;
            }

            while (reservationOrder.isUnfilled() && !counterPriceLevel.isEmpty()) {
                Order restingOrder = counterPriceLevel.peekOrder();

                // 예약 주문 실행 조건 체크(가격 외 시간, 트리거 조건 등)
                if (!tradingDomainService.isReservationOrderExecutable(reservationOrder, restingOrder.getOrderPrice())) {
                    log.info("Reservation order execution condition not met. Stopping matching.");
                    break;
                }

                // 현재 시간 기준 만료 여부 재검증
                if (reservationOrder.isExpired(LocalDateTime.now(ZoneOffset.UTC))) {
                    log.info("Reservation order expired during matching: {}", reservationOrder.getId().getValue());
                    break;
                }

                Quantity execQty = tradingDomainService.applyOrder(reservationOrder, restingOrder.getRemainingQuantity());
                tradingDomainService.applyOrder(restingOrder, execQty);
                OrderPrice executionPrice = new OrderPrice(priceLevel.getValue());
                FeeAmount feeAmount = finalFeeRate.calculateFeeAmount(executionPrice, execQty);

                TradingRecordedEvent tradeEvent = tradingDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        reservationOrder.getMarketId(),
                        reservationOrder.getUserId(),
                        reservationOrder.getId(),
                        executionPrice,
                        execQty,
                        reservationOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        finalFeeRate
                );

                tradingTradeRecordRepository.saveTrade(tradeEvent.getDomainType());
                trades.add(tradeEvent);

                log.info("Executed trade: {} qty at price {}", execQty.getValue(), priceLevel.getValue());
                if (restingOrder.isFilled()) {
                    counterPriceLevel.popOrder();
                }
                if (reservationOrder.isFilled()) {
                    break;
                }
            }
            if (reservationOrder.isFilled()) {
                break;
            }
        }

        // 매칭 후 처리: 남은 수량 & 만료 여부 체크
        if (reservationOrder.isUnfilled()) {
            if (reservationOrder.isExpired(LocalDateTime.now(ZoneOffset.UTC))) {
                // 만료된 예약 주문은 저장하지 않고 종료
                log.info("Reservation order expired after matching, not saved: {}", reservationOrder.getId().getValue());
            } else {
                // 남은 수량 있으면 저장 (다음 실행을 위해)
                tradingOrderRedisPort.saveReservationOrder(
                        RedisKeyPrefix.reservation(reservationOrder.getMarketId().getValue(),
                                reservationOrder.getId().getValue()), reservationOrder);
                log.info("Reservation order partially/unfilled → saved with remaining qty {}",
                        reservationOrder.getRemainingQuantity().getValue());
            }
        } else {
            tradingRepository.saveReservationOrder(reservationOrder);
            log.info("Reservation order fully filled: {}", reservationOrder.getId().getValue());
        }

        return trades;
    }

}
