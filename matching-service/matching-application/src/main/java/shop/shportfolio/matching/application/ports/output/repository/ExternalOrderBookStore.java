package shop.shportfolio.matching.application.ports.output.repository;

import shop.shportfolio.matching.domain.entity.MatchingOrderBook;

import java.util.Collection;

public interface ExternalOrderBookStore {

    MatchingOrderBook getOrderBook(String marketId);

    void putOrderBook(String marketId, MatchingOrderBook matchingOrderBook);

    void deleteOrderBook(String marketId);

    Collection<String> getAllMarketIds();

    Object getLock(String marketId);

    void clear();
}
