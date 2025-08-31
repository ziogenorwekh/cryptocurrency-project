package shop.shportfolio.matching.application.memorystore;

import org.springframework.stereotype.Component;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExternalOrderBookMemoryStore {

    private final Map<String, MatchingOrderBook> marketOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, Object> marketLocks = new ConcurrentHashMap<>();

    public MatchingOrderBook getOrderBook(String marketId) {
        return marketOrderBooks.get(marketId);
    }

    public void putOrderBook(String marketId, MatchingOrderBook matchingOrderBook) {
        marketOrderBooks.put(marketId, matchingOrderBook);
    }

    public void deleteOrderBook(String marketId) {
        marketOrderBooks.remove(marketId);
    }

    // 마켓별 lock 객체 가져오기
    public Object getLock(String marketId) {
        return marketLocks.computeIfAbsent(marketId, k -> new Object());
    }

    public void clear() {
        marketOrderBooks.clear();
    }
}
