//package shop.shportfolio.trading.application.handler;
//
//import lombok.extern.slf4j.Slf4j;
//import shop.shportfolio.common.domain.valueobject.*;
//import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
//import shop.shportfolio.trading.application.policy.FeePolicy;
//import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
//import shop.shportfolio.trading.domain.OrderDomainService;
//import shop.shportfolio.trading.domain.entity.*;
//import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
//import shop.shportfolio.trading.domain.valueobject.TickPrice;
//import shop.shportfolio.common.domain.valueobject.TradeId;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Slf4j
//@Deprecated
//public class OrderBookMarketMatchingEngine {
//
//    private final OrderDomainService orderDomainService;
//    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
//    private final CouponInfoTrackHandler couponInfoTrackHandler;
//    private final FeePolicy feePolicy;
//    private final TradingTradeRecordRepositoryPort testTradingTradeRecordRepositoryPort;
//
//    public OrderBookMarketMatchingEngine(OrderDomainService orderDomainService,
//                                         TradingOrderRepositoryPort tradingOrderRepositoryPort,
//                                         CouponInfoTrackHandler couponInfoTrackHandler,
//                                         FeePolicy feePolicy,
//                                         TradingTradeRecordRepositoryPort testTradingTradeRecordRepositoryPort) {
//        this.orderDomainService = orderDomainService;
//        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
//        this.couponInfoTrackHandler = couponInfoTrackHandler;
//        this.feePolicy = feePolicy;
//        this.testTradingTradeRecordRepositoryPort = testTradingTradeRecordRepositoryPort;
//    }
//
//    private List<TradingRecordedEvent> execBidMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
//        return execMarketOrder(marketOrder, orderBook.getBuyPriceLevels());
//    }
//
//    private List<TradingRecordedEvent> execAskMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
//        return execMarketOrder(marketOrder, orderBook.getSellPriceLevels());
//    }
//
//    private List<TradingRecordedEvent> execMarketOrder(
//            MarketOrder marketOrder,
//            NavigableMap<TickPrice, PriceLevel> priceLevels) {
//        List<TradingRecordedEvent> trades = new ArrayList<>();
//        Optional<CouponInfo> couponInfoOptional = couponInfoTrackHandler.trackCouponInfo(marketOrder.getUserId());
//
//        FeeRate baseFeeRate = feePolicy.calculateFeeRate(marketOrder.getOrderSide());
//        FeeRate finalFeeRate = baseFeeRate;
//
//        if (couponInfoOptional.isPresent()) {
//            CouponInfo couponInfo = couponInfoOptional.get();
//            if (!couponInfo.getUsageExpiryDate().isExpired()) {
//                BigDecimal discountRatio = couponInfo.getFeeDiscount().getRatio();
//                finalFeeRate = baseFeeRate.applyDiscount(discountRatio);
//                log.info("Coupon applied: userId={}, discount={}", marketOrder.getUserId().getValue(), discountRatio);
//            }
//        }
//
//        log.info("Start executing MarketOrder. OrderId={}, RemainingQty={}",
//                marketOrder.getId().getValue(), marketOrder.getRemainingQuantity().getValue());
//
//        while (marketOrder.isUnfilled() && !priceLevels.isEmpty()) {
//            Map.Entry<TickPrice, PriceLevel> entry = priceLevels.firstEntry();
//            PriceLevel priceLevel = entry.getValue();
//
//            while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
//                Order restingOrder = priceLevel.peekOrder();
//
//                Quantity execQty = orderDomainService.applyOrder(marketOrder, restingOrder.getRemainingQuantity());
//                orderDomainService.applyOrder(restingOrder, execQty);
//
//                OrderPrice executionPrice = new OrderPrice(entry.getKey().getValue());
//                FeeAmount feeAmount = finalFeeRate.calculateFeeAmount(executionPrice, execQty);
//                log.info("executionPrice: {}, execQty: {}, baseFeeRate: {}, discountRatio: {}, finalFeeRate: {}, feeAmount: {}",
//                        executionPrice.getValue(),
//                        execQty.getValue(),
//                        baseFeeRate.getRate(),
//                        couponInfoOptional.map(c -> c.getFeeDiscount().getRatio()).orElse(BigDecimal.ZERO),
//                        finalFeeRate.getRate(),
//                        feeAmount.getValue());
//
//                TradingRecordedEvent tradeEvent = orderDomainService.createTrade(
//                        new TradeId(UUID.randomUUID()),
//                        marketOrder.getMarketId(),
//                        marketOrder.getUserId(),
//
//                        marketOrder.getId(),
//                        executionPrice,
//                        execQty,
//                        marketOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
//                        feeAmount,
//                        finalFeeRate
//                );
//
//                testTradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
//                trades.add(tradeEvent);
//
//                log.info("Executed trade: {} qty at price {}", execQty.getValue(), entry.getKey().getValue());
//
//                if (restingOrder.isFilled()) {
//                    priceLevel.popOrder();
//                }
//
//                if (marketOrder.isFilled()) {
//                    log.info("filled MarketOrderId : {}", marketOrder.getId().getValue());
//                    tradingOrderRepositoryPort.saveMarketOrder(marketOrder);
//                    break;
//                }
//            }
//
//            if (priceLevel.isEmpty()) {
//                priceLevels.remove(entry.getKey());
//            }
//        }
//
//        if (marketOrder.isUnfilled()) {
//            log.info("market is unfilled");
//            orderDomainService.cancelOrder(marketOrder);
//            log.info("market is unfilled And Status Update: {}",
//                    marketOrder.getOrderStatus().name());
//            log.info("marketOrder is unfilled Id : {}", marketOrder.getId().getValue());
//            tradingOrderRepositoryPort.saveMarketOrder(marketOrder);
//        }
//
//        return trades;
//    }
//}
//
//
//
