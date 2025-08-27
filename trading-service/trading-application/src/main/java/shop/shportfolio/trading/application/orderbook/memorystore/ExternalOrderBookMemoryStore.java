package shop.shportfolio.trading.application.orderbook.memorystore;

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

    public boolean containsMarket(String marketId) {
        return marketOrderBooks.containsKey(marketId);
    }

    public Map<String, OrderBook> getAllOrderBooks() {
        return marketOrderBooks;
    }

    public Object getMarketLock(String marketId) {
        return marketLocks.computeIfAbsent(marketId, k -> new Object());
    }
}
