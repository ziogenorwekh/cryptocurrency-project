package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class LimitOrderMatchingStrategy implements OrderMatchingStrategy<LimitOrder> {

    private final UserBalanceDomainService userBalanceDomainService;
    private final TradeDomainService tradeDomainService;
    private final OrderDomainService orderDomainService;
    private final TradingOrderRepositoryPort tradingRepository;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepository;
    private final TradingOrderRedisPort tradingOrderRedisPort;
    private final CouponInfoHandler couponInfoHandler;
    private final FeePolicy feePolicy;
    private final TradingUserBalanceRepositoryPort tradingUserBalanceRepository;

    public LimitOrderMatchingStrategy(UserBalanceDomainService userBalanceDomainService,
                                      TradeDomainService tradeDomainService,
                                      OrderDomainService orderDomainService,
                                      TradingOrderRepositoryPort tradingRepository,
                                      TradingTradeRecordRepositoryPort tradingTradeRecordRepository,
                                      TradingOrderRedisPort tradingOrderRedisPort,
                                      CouponInfoHandler couponInfoHandler,
                                      FeePolicy feePolicy, TradingUserBalanceRepositoryPort tradingUserBalanceRepository) {
        this.userBalanceDomainService = userBalanceDomainService;
        this.tradeDomainService = tradeDomainService;
        this.orderDomainService = orderDomainService;
        this.tradingRepository = tradingRepository;
        this.tradingTradeRecordRepository = tradingTradeRecordRepository;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
        this.couponInfoHandler = couponInfoHandler;
        this.feePolicy = feePolicy;
        this.tradingUserBalanceRepository = tradingUserBalanceRepository;
    }

    @Override
    public List<TradingRecordedEvent> match(OrderBook orderBook, LimitOrder limitOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        UserBalance userBalance = getUserBalanceOrThrow(limitOrder.getUserId());

        TickPrice tickPrice = TickPrice.of(limitOrder.getOrderPrice().getValue(), orderBook.getMarketItemTick().getValue());

        if (!orderDomainService.canMatchPrice(limitOrder, tickPrice)) {
            return trades;
        }

        PriceLevel priceLevel = getCounterPriceLevel(orderBook, limitOrder, tickPrice);
        if (priceLevel == null || priceLevel.isEmpty()) {
            return trades;
        }

        FeeRate finalFeeRate = calculateFinalFeeRate(limitOrder.getUserId(), limitOrder.getOrderSide());

        while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
            Order restingOrder = priceLevel.peekOrder();
            TradingRecordedEvent tradeEvent = executeTrade(limitOrder, restingOrder, tickPrice, finalFeeRate, userBalance);
            trades.add(tradeEvent);

            if (restingOrder.isFilled()) {
                priceLevel.popOrder();
            }
        }

        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        cleanupEmptyPriceLevel(counterPriceLevels, tickPrice);

        handleUnfilledOrFilledLimitOrder(limitOrder, orderBook, tickPrice);

        tradingUserBalanceRepository.saveUserBalance(userBalance);

        return trades;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    private UserBalance getUserBalanceOrThrow(UserId userId) {
        return tradingUserBalanceRepository.findUserBalanceByUserId(userId.getValue())
                .orElseThrow(() -> new UserBalanceNotFoundException(
                        String.format("User balance not found for userId %s", userId.getValue())));
    }

    private FeeRate calculateFinalFeeRate(UserId userId, OrderSide orderSide) {
        FeeRate baseFeeRate = feePolicy.calculateDefualtFeeRate(orderSide);
        Optional<CouponInfo> couponInfoOptional = couponInfoHandler.trackCouponInfo(userId);

        if (couponInfoOptional.isPresent()) {
            CouponInfo couponInfo = couponInfoOptional.get();
            if (!couponInfo.getUsageExpiryDate().isExpired()) {
                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
                BigDecimal discountDecimal = discountRatio.divide(BigDecimal.valueOf(100));
                log.info("Coupon applied: userId={}, discount={}", userId.getValue(), discountDecimal);
                return baseFeeRate.applyDiscount(discountDecimal);
            }
        }
        return baseFeeRate;
    }

    private PriceLevel getCounterPriceLevel(OrderBook orderBook, LimitOrder limitOrder, TickPrice tickPrice) {
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();
        return counterPriceLevels.get(tickPrice);
    }

    private TradingRecordedEvent executeTrade(LimitOrder limitOrder,
                                              Order restingOrder,
                                              TickPrice tickPrice,
                                              FeeRate feeRate,
                                              UserBalance userBalance) {
        Quantity execQty = orderDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
        orderDomainService.applyOrder(restingOrder, execQty);

        OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());
        FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

        TradingRecordedEvent tradeEvent = tradeDomainService.createTrade(
                new TradeId(UUID.randomUUID()),
                limitOrder.getMarketId(),
                limitOrder.getUserId(),
                limitOrder.getId(),
                executionPrice,
                execQty,
                limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                feeAmount,
                feeRate
        );

        Trade trade = tradingTradeRecordRepository.saveTrade(tradeEvent.getDomainType());

        BigDecimal totalAmount = trade.getOrderPrice().getValue()
                .multiply(trade.getQuantity().getValue())
                .add(trade.getFeeAmount().getValue());

        userBalanceDomainService.deductBalanceForTrade(userBalance, limitOrder.getId(), Money.of(totalAmount));

        tradingUserBalanceRepository.saveUserBalance(userBalance);

        log.info("Executed trade: {} qty at price {}", execQty.getValue(), tickPrice.getValue());

        return tradeEvent;
    }

    private void cleanupEmptyPriceLevel(NavigableMap<TickPrice, PriceLevel> priceLevels, TickPrice tickPrice) {
        PriceLevel priceLevel = priceLevels.get(tickPrice);
        if (priceLevel == null || priceLevel.isEmpty()) {
            priceLevels.remove(tickPrice);
        }
    }

    private void handleUnfilledOrFilledLimitOrder(LimitOrder limitOrder, OrderBook orderBook, TickPrice tickPrice) {
        if (limitOrder.isUnfilled()) {
            tradingRepository.saveLimitOrder(limitOrder);
            tradingOrderRedisPort.saveLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(),
                    limitOrder.getId().getValue()), limitOrder);

            NavigableMap<TickPrice, PriceLevel> ownPriceLevels = limitOrder.getOrderSide().isBuy()
                    ? orderBook.getBuyPriceLevels()
                    : orderBook.getSellPriceLevels();

            PriceLevel ownPriceLevel = ownPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice));
            ownPriceLevel.addOrder(limitOrder);

            log.info("Limit order {} partially/unfilled → added to orderbook at price {}",
                    limitOrder.getId().getValue(), tickPrice.getValue());
        } else if (limitOrder.isFilled()) {
            tradingOrderRedisPort.deleteLimitOrder(RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(),
                    limitOrder.getId().getValue()));
            tradingRepository.saveLimitOrder(limitOrder);
        }
    }
}
