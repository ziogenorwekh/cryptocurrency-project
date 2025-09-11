//package shop.shportfolio.trading.application.orderbook.manager;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
//import shop.shportfolio.trading.application.exception.MarketPausedException;
//import shop.shportfolio.trading.application.orderbook.memorystore.ExternalOrderBookMemoryStore;
//import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
//import shop.shportfolio.trading.domain.OrderDomainService;
//import shop.shportfolio.trading.domain.TradeDomainService;
//import shop.shportfolio.trading.domain.entity.*;
//import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
//import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
//import shop.shportfolio.trading.domain.entity.trade.Trade;
//
//import java.util.*;
//
//@Slf4j
//@Component
//public class OrderBookManager {
//
//    private final OrderDomainService orderDomainService;
//    private final TradingOrderRedisPort tradingOrderRedisPort;
//    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
//    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
//    private final TradeDomainService tradeDomainService;
//
//    @Autowired
//    public OrderBookManager(OrderDomainService orderDomainService,
//                            TradingOrderRedisPort tradingOrderRedisPort,
//                            TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort,
//                            TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort, TradeDomainService tradeDomainService) {
//        this.orderDomainService = orderDomainService;
//        this.tradingOrderRedisPort = tradingOrderRedisPort;
//        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
//        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
//        this.tradeDomainService = tradeDomainService;
//    }
//
//
//    public MarketItem findMarketItemById(String marketId) {
//        MarketItem marketItem = tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId).orElseThrow(() ->
//                {
//                    log.info("marketId:{} not found", marketId);
//                    return new MarketItemNotFoundException(String.format("MarketItem with id %s not found",
//                            marketId));
//                }
//        );
//        if (!marketItem.isActive()) {
//            throw new MarketPausedException(String.format("MarketItem with id %s is not active", marketId));
//        }
//        return marketItem;
//    }
//
//    public OrderBook loadAdjustedOrderBook(String marketId) {
//        OrderBook orderBook = ExternalOrderBookMemoryStore.getInstance().getOrderBook(marketId);
//        List<LimitOrder> orders = tradingOrderRedisPort.findLimitOrdersByMarketId(marketId);
//        OrderBook adjustedOrderBookAddLimitOrder = adjustedOrderBookByLimitOrders(orderBook, orders);
//        List<Trade> trades = tradingTradeRecordRepositoryPort.findTradesByMarketId(marketId);
//        trades.forEach(trade -> tradeDomainService.applyExecutedTrade(adjustedOrderBookAddLimitOrder, trade));
//        return adjustedOrderBookAddLimitOrder;
//    }
//
//    private OrderBook adjustedOrderBookByLimitOrders(OrderBook orderBook, List<LimitOrder> orders) {
//
//        Set<String> existingOrderIds = new HashSet<>();
//        orderBook.getBuyPriceLevels().values().forEach(priceLevel ->
//                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
//        );
//        orderBook.getSellPriceLevels().values().forEach(priceLevel ->
//                priceLevel.getOrders().forEach(order -> existingOrderIds.add(order.getId().getValue()))
//        );
//
//        orders.forEach(limitOrder -> {
//            if (!existingOrderIds.contains(limitOrder.getId().getValue())) {
//                orderDomainService.addOrderbyOrderBook(orderBook, limitOrder);
//                existingOrderIds.add(limitOrder.getId().getValue());
//            }
//        });
//        return orderBook;
//    }
//
//}
