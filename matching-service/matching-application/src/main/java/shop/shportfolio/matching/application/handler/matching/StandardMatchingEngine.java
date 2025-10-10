package shop.shportfolio.matching.application.handler.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.application.exception.UnSupportOrderTypeException;
import shop.shportfolio.matching.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.matching.application.ports.output.repository.ExternalOrderBookStore;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedPublisher;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Slf4j
@Component
public class StandardMatchingEngine implements MatchingEngine {

    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final OrderStore orderStore;
    private final MatchedPublisher matchedPublisher;
    private final ExternalOrderBookStore externalOrderBookStore;

    @Autowired
    public StandardMatchingEngine(List<OrderMatchingStrategy<? extends Order>> strategies,
                                  OrderStore orderStore,
                                  MatchedPublisher matchedPublisher,
                                  ExternalOrderBookStore externalOrderBookStore) {
        this.strategies = strategies;
        this.orderStore = orderStore;
        this.matchedPublisher = matchedPublisher;
        this.externalOrderBookStore = externalOrderBookStore;
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {
        MatchedContext<MarketOrder> matchedContext = matchOrder(marketOrder);
        // 시장 상황 부족 시 취소 처리
        orderStore.removeMarketOrder(matchedContext.getOrder());
        matchedContext.getTradeCreatedEvents().forEach(matchedPublisher::publish);
    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {
        MatchedContext<LimitOrder> matchedContext = matchOrder(limitOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderStore.removeLimitOrder(limitOrder);
        }
        matchedContext.getTradeCreatedEvents().forEach(matchedPublisher::publish);
    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {
        MatchedContext<ReservationOrder> matchedContext = matchOrder(reservationOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderStore.removeReservationOrder(reservationOrder);
        }
        matchedContext.getTradeCreatedEvents().forEach(matchedPublisher::publish);
    }

    @SuppressWarnings("unchecked")
    private <T extends Order> OrderMatchingStrategy<T> findStrategy(T order) {
        return (OrderMatchingStrategy<T>) strategies.stream()
                .filter(s -> s.supports(order))
                .findFirst()
                .orElseThrow(() -> new UnSupportOrderTypeException("No strategy for order type: " + order.getOrderType()));
    }

    private <T extends Order> MatchedContext<T> matchOrder(T order) {
        OrderMatchingStrategy<T> strategy = findStrategy(order);
        MatchingOrderBook matchingOrderBook = externalOrderBookStore.getOrderBook(order.getMarketId().getValue());

        Object marketLock = externalOrderBookStore.getLock(order.getMarketId().getValue());
        MatchedContext<T> matchedContext;
        synchronized (marketLock) {
//            log.info("orderBook buy level size is -> {}", matchingOrderBook.getBuyPriceLevels().size());
//            log.info("orderBook sell level size is -> {}", matchingOrderBook.getSellPriceLevels().size());
            matchedContext = strategy.match(matchingOrderBook, order);
        }

        return matchedContext;
    }
}
