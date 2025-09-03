package shop.shportfolio.matching.application.handler.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.application.exception.UnSupportOrderTypeException;
import shop.shportfolio.matching.application.handler.OrderBookManager;
import shop.shportfolio.matching.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
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
    private final OrderBookManager orderBookManager;
    private final OrderMemoryStore orderMemoryStore;
    private final MatchedPublisher matchedPublisher;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;

    @Autowired
    public StandardMatchingEngine(List<OrderMatchingStrategy<? extends Order>> strategies,
                                  OrderBookManager orderBookManager, OrderMemoryStore orderMemoryStore,
                                  MatchedPublisher matchedPublisher,
                                  ExternalOrderBookMemoryStore externalOrderBookMemoryStore) {
        this.strategies = strategies;
        this.orderBookManager = orderBookManager;
        this.orderMemoryStore = orderMemoryStore;
        this.matchedPublisher = matchedPublisher;
        this.externalOrderBookMemoryStore = externalOrderBookMemoryStore;
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {
        MatchedContext<MarketOrder> matchedContext = matchOrder(marketOrder);
        // 시장 상황 부족 시 취소 처리
        orderMemoryStore.removeMarketOrder(matchedContext.getOrder());
        matchedContext.getTradeCreatedEvents().forEach(matchedPublisher::publish);
    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {
        MatchedContext<LimitOrder> matchedContext = matchOrder(limitOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderMemoryStore.removeLimitOrder(limitOrder);
        }
        matchedContext.getTradeCreatedEvents().forEach(matchedPublisher::publish);
    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {
        MatchedContext<ReservationOrder> matchedContext = matchOrder(reservationOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderMemoryStore.removeReservationOrder(reservationOrder);
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
        MatchingOrderBook matchingOrderBook = orderBookManager.loadAdjustedOrderBook(order.getMarketId().getValue());

        Object marketLock = externalOrderBookMemoryStore.getOrderBook(order.getMarketId().getValue());
        MatchedContext<T> matchedContext;
        synchronized (marketLock) {
            log.info("orderBook buy level size is -> {}", matchingOrderBook.getBuyPriceLevels().size());
            log.info("orderBook sell level size is -> {}", matchingOrderBook.getSellPriceLevels().size());
            matchedContext = strategy.match(matchingOrderBook, order);
        }

        return matchedContext;
    }

}
