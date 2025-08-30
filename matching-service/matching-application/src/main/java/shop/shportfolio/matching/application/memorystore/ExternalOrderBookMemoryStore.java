package shop.shportfolio.matching.application.memorystore;

import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExternalOrderBookMemoryStore {

    private final Map<String, OrderBook> marketOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, Object> marketLocks = new ConcurrentHashMap<>();

    private static final ExternalOrderBookMemoryStore INSTANCE = new ExternalOrderBookMemoryStore();

    private ExternalOrderBookMemoryStore() {}

    public static ExternalOrderBookMemoryStore getInstance() {
        return INSTANCE;
    }

    public OrderBook getOrderBook(String marketId) {
        return marketOrderBooks.get(marketId);
    }

    public void putOrderBook(String marketId, OrderBook orderBook) {
        marketOrderBooks.put(marketId, orderBook);
    }

    public void deleteOrderBook(String marketId) {
        marketOrderBooks.remove(marketId);
    }
}
