package shop.shportfolio.trading.application.orderbook.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.orderbook.manager.OrderBookManager;
import shop.shportfolio.trading.application.orderbook.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.orderbook.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.trading.application.ports.output.kafka.TradePublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalancePublisher;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.util.List;

@Slf4j
@Component
public class OrderMatchingExecutorImpl implements OrderMatchingExecutor {

    private final OrderBookManager orderBookManager;
    private final TradePublisher tradePublisher;
    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final UserBalancePublisher userBalancePublisher;
    private final ExternalOrderBookMemoryStore orderBookStore = ExternalOrderBookMemoryStore.getInstance();
    public OrderMatchingExecutorImpl(
            OrderBookManager orderBookManager,
            TradePublisher tradePublisher,
            List<OrderMatchingStrategy<? extends Order>> strategies,
            UserBalancePublisher userBalancePublisher) {
        this.orderBookManager = orderBookManager;
        this.tradePublisher = tradePublisher;
        this.strategies = strategies;
        this.userBalancePublisher = userBalancePublisher;
    }

    @Override
    public List<TradeCreatedEvent> executeMarketOrder(MarketOrder marketOrder) {
        return execute(marketOrder);
    }

    @Override
    public List<TradeCreatedEvent> executeLimitOrder(LimitOrder limitOrder) {
        return execute(limitOrder);
    }

    @Override
    public List<TradeCreatedEvent> executeReservationOrder(ReservationOrder reservationOrder) {
        return execute(reservationOrder);
    }

    @SuppressWarnings("unchecked")
    private <T extends Order> OrderMatchingStrategy<T> findStrategy(T order) {
        return (OrderMatchingStrategy<T>) strategies.stream()
                .filter(s -> s.supports(order))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy for order type: " + order.getOrderType()));
    }

    private OrderBook extractOrderBook(String marketId) {
        var marketItem = orderBookManager.findMarketItemById(marketId);
        var orderBook = orderBookManager.loadAdjustedOrderBook(marketItem.getId().getValue());
        log.info("OrderBook loaded: marketId={}, buyLevels={}, sellLevels={}",
                marketId,
                orderBook.getBuyPriceLevels().size(),
                orderBook.getSellPriceLevels().size());
        return orderBook;
    }

    private <T extends Order> List<TradeCreatedEvent> execute(T order) {
        log.info("[{}] Received {} order: userId={}, marketId={}, orderPrice={}",
                order.getId().getValue(),
                order.getOrderType(),
                order.getUserId().getValue(),
                order.getMarketId().getValue(),
                order instanceof LimitOrder ? ((LimitOrder) order).getOrderPrice().getValue() : "MarketOrder");

        OrderBook orderBook = this.extractOrderBook(order.getMarketId().getValue());
        Object marketLock = orderBookStore.getMarketLock(order.getMarketId().getValue());

        synchronized (marketLock) {
            OrderMatchingStrategy<T> strategy = findStrategy(order);
            TradeMatchingContext matchingContext = strategy.match(orderBook, order);

            log.info("[{}] Matching finished: executed trades={}", order.getId().getValue(),
                    matchingContext.getTradingRecordedEvents().size());
            matchingContext.getTradingRecordedEvents().forEach(trade ->
                    log.info("[{}] Trade executed: {}", order.getId().getValue(), trade.getDomainType().getTransactionType()));

            userBalancePublisher.publish(matchingContext.getUserBalanceUpdatedEvent());

            matchingContext.getTradingRecordedEvents().forEach(tradePublisher::publish);
            log.info("[{}] UserBalanceUpdatedEvent published: userId={}, type={}",
                    order.getId().getValue(),
                    matchingContext.getUserBalanceUpdatedEvent().getDomainType().getUserId().getValue(),
                    matchingContext.getUserBalanceUpdatedEvent().getMessageType());
            return matchingContext.getTradingRecordedEvents();
        }
    }
}
