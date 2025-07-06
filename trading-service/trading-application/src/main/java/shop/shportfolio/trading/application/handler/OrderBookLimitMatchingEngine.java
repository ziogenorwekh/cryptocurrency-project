package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class OrderBookLimitMatchingEngine {

    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryPort tradingRepository;
    private final MarketDataRedisPort marketDataRedisPort;
    private final CouponInfoTrackHandler couponInfoTrackHandler;
    private final FeePolicy feePolicy;

    @Autowired
    public OrderBookLimitMatchingEngine(TradingDomainService tradingDomainService,
                                        TradingRepositoryPort tradingRepository,
                                        MarketDataRedisPort marketDataRedisPort,
                                        CouponInfoTrackHandler couponInfoTrackHandler,
                                        FeePolicy feePolicy) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepository = tradingRepository;
        this.marketDataRedisPort = marketDataRedisPort;
        this.couponInfoTrackHandler = couponInfoTrackHandler;
        this.feePolicy = feePolicy;
    }


    public List<TradingRecordedEvent> execBidLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {
        return execLimitOrder(limitOrder, orderBook, orderBook.getBuyPriceLevels());
    }

    public List<TradingRecordedEvent> execAskLimitOrder(OrderBook orderBook, LimitOrder limitOrder) {
        return execLimitOrder(limitOrder, orderBook, orderBook.getSellPriceLevels());
    }

    private List<TradingRecordedEvent> execLimitOrder(
            LimitOrder limitOrder,
            OrderBook orderBook,
            NavigableMap<TickPrice, PriceLevel> counterPriceLevels) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        // 주문가에 해당하는 가격레벨만 조회
        TickPrice tickPrice = TickPrice.of(limitOrder.getOrderPrice().getValue(), orderBook.getMarketItemTick().getValue());
        PriceLevel priceLevel = counterPriceLevels.get(tickPrice);

        if (priceLevel == null || priceLevel.isEmpty()) {
            // 매칭할 가격 레벨이 없으면 바로 종료
            return trades;
        }

        Optional<CouponInfo> couponInfoOptional = couponInfoTrackHandler.trackCouponInfo(limitOrder.getUserId());
        FeeRate baseFeeRate = feePolicy.calculateFeeRate(limitOrder.getOrderSide());
        FeeRate finalFeeRate = baseFeeRate;

        if (couponInfoOptional.isPresent()) {
            CouponInfo couponInfo = couponInfoOptional.get();
            if (!couponInfo.getUsageExpiryDate().isExpired()) {
                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
                // 30% 할인이라면 0.3으로 바꿔서 적용해야 함
                BigDecimal discountDecimal = discountRatio.divide(BigDecimal.valueOf(100));
                finalFeeRate = baseFeeRate.applyDiscount(discountDecimal);
                log.info("Coupon applied: userId={}, discount={}", limitOrder.getUserId().getValue(), discountDecimal);
            }
        }

        log.info("Start executing LimitOrder. OrderId={}, RemainingQty={}",
                limitOrder.getId().getValue(), limitOrder.getRemainingQuantity().getValue());

        if (!tradingDomainService.canMatchPrice(limitOrder, tickPrice)) {
            // 지정가에 맞지 않으면 매칭하지 않음
            return trades;
        }

        while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
            Order restingOrder = priceLevel.peekOrder();

            Quantity execQty = tradingDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
            tradingDomainService.applyOrder(restingOrder, execQty);

            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());
            FeeAmount feeAmount = finalFeeRate.calculateFeeAmount(executionPrice, execQty);

            TradingRecordedEvent tradeEvent = tradingDomainService.createTrade(
                    new TradeId(UUID.randomUUID()),
                    limitOrder.getUserId(),
                    limitOrder.getId(),
                    executionPrice,
                    execQty,
                    limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                    feeAmount,
                    finalFeeRate
            );

            tradingRepository.saveTrade(tradeEvent.getDomainType());
            trades.add(tradeEvent);

            log.info("Executed trade: {} qty at price {}", execQty.getValue(), tickPrice.getValue());

            if (restingOrder.isFilled()) {
                priceLevel.popOrder();
            }
        }

        if (priceLevel.isEmpty()) {
            counterPriceLevels.remove(tickPrice);
        }

        if (limitOrder.isUnfilled()) {
            tradingRepository.saveLimitOrder(limitOrder);
            marketDataRedisPort.saveLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(),
                    limitOrder.getId().getValue()), limitOrder);

            NavigableMap<TickPrice, PriceLevel> ownPriceLevels = limitOrder.getOrderSide().isBuy() ?
                    orderBook.getBuyPriceLevels() : orderBook.getSellPriceLevels();

            PriceLevel ownPriceLevel = ownPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice));
            ownPriceLevel.addOrder(limitOrder);

            log.info("Limit order {} partially/unfilled → added to orderbook at price {}",
                    limitOrder.getId().getValue(), tickPrice.getValue());
        }

        if (limitOrder.isFilled()) {
            marketDataRedisPort.deleteLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(),
                    limitOrder.getId().getValue()));
            tradingRepository.saveLimitOrder(limitOrder);
        }

        return trades;
        }
}
