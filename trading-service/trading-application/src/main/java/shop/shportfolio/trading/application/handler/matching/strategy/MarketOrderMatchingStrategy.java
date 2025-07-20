package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class MarketOrderMatchingStrategy implements OrderMatchingStrategy<MarketOrder> {


    private final UserBalanceDomainService userBalanceDomainService;
    private final TradeDomainService tradeDomainService;
    private final OrderDomainService orderDomainService;
    private final TradingOrderRepositoryPort tradingRepository;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepository;
    private final CouponInfoHandler couponInfoHandler;
    private final FeePolicy feePolicy;
    private final TradingUserBalanceRepositoryPort tradingUserBalanceRepository;

    public MarketOrderMatchingStrategy(UserBalanceDomainService userBalanceDomainService,
                                       TradeDomainService tradeDomainService,
                                       OrderDomainService orderDomainService,
                                       TradingOrderRepositoryPort tradingRepository,
                                       TradingTradeRecordRepositoryPort tradingTradeRecordRepository,
                                       CouponInfoHandler couponInfoHandler,
                                       FeePolicy feePolicy, TradingUserBalanceRepositoryPort tradingUserBalanceRepository) {
        this.userBalanceDomainService = userBalanceDomainService;
        this.tradeDomainService = tradeDomainService;
        this.orderDomainService = orderDomainService;
        this.tradingRepository = tradingRepository;
        this.tradingTradeRecordRepository = tradingTradeRecordRepository;
        this.couponInfoHandler = couponInfoHandler;
        this.feePolicy = feePolicy;
        this.tradingUserBalanceRepository = tradingUserBalanceRepository;
    }


    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public List<TradingRecordedEvent> match(OrderBook orderBook, MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> priceLevels;
        if (marketOrder.isBuyOrder()) {
            priceLevels = orderBook.getSellPriceLevels();
        } else {
            priceLevels = orderBook.getBuyPriceLevels();
        }

        Optional<CouponInfo> couponInfoOptional = couponInfoHandler.trackCouponInfo(marketOrder.getUserId());

        FeeRate baseFeeRate = feePolicy.calculateDefualtFeeRate(marketOrder.getOrderSide());
        FeeRate finalFeeRate = baseFeeRate;

        if (couponInfoOptional.isPresent()) {
            CouponInfo couponInfo = couponInfoOptional.get();
            if (!couponInfo.getUsageExpiryDate().isExpired()) {
                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
                finalFeeRate = baseFeeRate.applyDiscount(discountRatio);
                log.info("Coupon applied: userId={}, discount={}", marketOrder.getUserId().getValue(), discountRatio);
            }
        }

        log.info("Start executing MarketOrder. OrderId={}, RemainingQty={}",
                marketOrder.getId().getValue(), marketOrder.getRemainingQuantity().getValue());

        while (marketOrder.isUnfilled() && !priceLevels.isEmpty()) {
            Map.Entry<TickPrice, PriceLevel> entry = priceLevels.firstEntry();
            PriceLevel priceLevel = entry.getValue();

            while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();

                Quantity execQty = orderDomainService.applyOrder(marketOrder, restingOrder.getRemainingQuantity());
                orderDomainService.applyOrder(restingOrder, execQty);

                OrderPrice executionPrice = new OrderPrice(entry.getKey().getValue());
                FeeAmount feeAmount = finalFeeRate.calculateFeeAmount(executionPrice, execQty);
                log.info("executionPrice: {}, execQty: {}, baseFeeRate: {}, discountRatio: {}, finalFeeRate: {}, feeAmount: {}",
                        executionPrice.getValue(),
                        execQty.getValue(),
                        baseFeeRate.getRate(),
                        couponInfoOptional.map(c -> c.getFeeDiscount().getRatio()).orElse(BigDecimal.ZERO),
                        finalFeeRate.getRate(),
                        feeAmount.getValue());

                TradingRecordedEvent tradeEvent = tradeDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        marketOrder.getMarketId(),
                        marketOrder.getUserId(),
                        marketOrder.getId(),
                        executionPrice,
                        execQty,
                        marketOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        finalFeeRate
                );

                Trade trade = tradingTradeRecordRepository.saveTrade(tradeEvent.getDomainType());
//                UserBalance userBalance = tradingUserBalanceRepository.findUserBalanceByUserId(
//                                marketOrder.getUserId().getValue())
//                        .orElseThrow(() -> new UserBalanceNotFoundException(
//                                String.format("User balance not found for reservation order %s",
//                                        marketOrder.getUserId().getValue())));
//                BigDecimal totalAmount = trade.getOrderPrice().getValue().multiply(trade.getQuantity().getValue())
//                        .add(trade.getFeeAmount().getValue());
//                userBalanceDomainService.deductBalanceForTrade(userBalance, Money.of(totalAmount));
//                tradingUserBalanceRepository.saveUserBalance(userBalance);
                trades.add(tradeEvent);

                log.info("Executed trade: {} qty at price {}", execQty.getValue(), entry.getKey().getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }

                if (marketOrder.isFilled()) {
                    log.info("filled MarketOrderId : {}", marketOrder.getId().getValue());
                    tradingRepository.saveMarketOrder(marketOrder);
                    break;
                }
            }

            if (priceLevel.isEmpty()) {
                priceLevels.remove(entry.getKey());
            }
        }

        if (marketOrder.isUnfilled()) {
            log.info("market is unfilled");
            orderDomainService.cancelOrder(marketOrder);
            log.info("market is unfilled And Status Update: {}",
                    marketOrder.getOrderStatus().name());
            log.info("marketOrder is unfilled Id : {}", marketOrder.getId().getValue());
            tradingRepository.saveMarketOrder(marketOrder);
        }

        return trades;
    }

}
