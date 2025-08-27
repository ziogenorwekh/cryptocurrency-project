package shop.shportfolio.trading.application.orderbook;

import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExternalOrderBookMemoryStore {

    private final Map<String, OrderBook> marketOrderBooks = new ConcurrentHashMap<>();
    private static final ExternalOrderBookMemoryStore INSTANCE = new ExternalOrderBookMemoryStore();

    private ExternalOrderBookMemoryStore() {}

    public static ExternalOrderBookMemoryStore getInstance() {
        return INSTANCE;
    }

    /**
     * marketId로 OrderBook 가져오기
     */
    public OrderBook getOrderBook(String marketId) {
        return marketOrderBooks.get(marketId);
    }

    /**
     * marketId 기준 OrderBook 저장/갱신
     */
    public void putOrderBook(String marketId, OrderBook orderBook) {
        marketOrderBooks.put(marketId, orderBook);
    }

    /**
     * marketId 존재 여부 확인
     */
    public boolean containsMarket(String marketId) {
        return marketOrderBooks.containsKey(marketId);
    }

    /**
     * 전체 MarketId와 OrderBook Map 반환
     */
    public Map<String, OrderBook> getAllOrderBooks() {
        return marketOrderBooks;
    }
}
