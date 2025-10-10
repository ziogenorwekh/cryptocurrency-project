package shop.shportfolio.matching.infrastructure.memory.store;

import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.exception.OrderBookNotFoundException;
import shop.shportfolio.matching.application.ports.output.repository.ExternalOrderBookStore;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExternalOrderBookMemoryStore implements ExternalOrderBookStore {

    private final Map<String, MatchingOrderBook> marketOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, Object> marketLocks = new ConcurrentHashMap<>();

    public MatchingOrderBook getOrderBook(String marketId) {
        MatchingOrderBook matchingOrderBook = marketOrderBooks.get(marketId);
        if (matchingOrderBook == null) {
            throw new OrderBookNotFoundException(String.format("OrderBook with id %s not found", marketId));
        }
        return matchingOrderBook;
    }

    public void putOrderBook(String marketId, MatchingOrderBook matchingOrderBook) {
        marketOrderBooks.put(marketId, matchingOrderBook);
    }

    public void deleteOrderBook(String marketId) {
        MatchingOrderBook matchingOrderBook = this.marketOrderBooks.get(marketId);
        if (matchingOrderBook == null) {
            throw new OrderBookNotFoundException(String.format("OrderBook with id %s not found", marketId));
        }
        marketOrderBooks.remove(marketId);
    }

    public Collection<String> getAllMarketIds() {
        return marketOrderBooks.keySet();
    }

    // 마켓별 lock 객체 가져오기
    public Object getLock(String marketId) {
        return marketLocks.computeIfAbsent(marketId, k -> new Object());
    }

    public void clear() {
        marketOrderBooks.clear();
    }
}
