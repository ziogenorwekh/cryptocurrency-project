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
import shop.shportfolio.matching.application.ports.output.kafka.MatchedKafkaPublisher;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.util.List;

@Slf4j
@Component
public class StandardMatchingEngine implements MatchingEngine {

    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final OrderBookManager orderBookManager;
    private final OrderMemoryStore orderMemoryStore;
    private final MatchedKafkaPublisher matchedKafkaPublisher;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore;

    @Autowired
    public StandardMatchingEngine(List<OrderMatchingStrategy<? extends Order>> strategies,
                                  OrderBookManager orderBookManager, MatchedKafkaPublisher matchedKafkaPublisher) {
        this.strategies = strategies;
        this.orderBookManager = orderBookManager;
        this.matchedKafkaPublisher = matchedKafkaPublisher;
        this.orderMemoryStore = OrderMemoryStore.getInstance();
        externalOrderBookMemoryStore = ExternalOrderBookMemoryStore.getInstance();
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {
        MatchedContext<MarketOrder> matchedContext = matchOrder(marketOrder);
        // 시장 상황 부족 시 취소 처리
        orderMemoryStore.removeMarketOrder(matchedContext.getOrder());
        matchedContext.getTradeCreatedEvents().forEach(matchedKafkaPublisher::publish);
    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {
        MatchedContext<LimitOrder> matchedContext = matchOrder(limitOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderMemoryStore.removeLimitOrder(limitOrder);
        }
        matchedContext.getTradeCreatedEvents().forEach(matchedKafkaPublisher::publish);
    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {
        MatchedContext<ReservationOrder> matchedContext = matchOrder(reservationOrder);
        if (matchedContext.getOrder().isFilled()) {
            orderMemoryStore.removeReservationOrder(reservationOrder);
        }
        matchedContext.getTradeCreatedEvents().forEach(matchedKafkaPublisher::publish);
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
        OrderBook orderBook = orderBookManager.loadAdjustedOrderBook(order.getMarketId().getValue(), orderMemoryStore);

        Object marketLock = externalOrderBookMemoryStore.getOrderBook(order.getMarketId().getValue());
        MatchedContext<T> matchedContext;
        synchronized (marketLock) {
            log.info("orderBook buy level size is -> {}", orderBook.getBuyPriceLevels().size());
            log.info("orderBook sell level size is -> {}", orderBook.getSellPriceLevels().size());
            matchedContext = strategy.match(orderBook, order);
        }

        return matchedContext;
    }

}
