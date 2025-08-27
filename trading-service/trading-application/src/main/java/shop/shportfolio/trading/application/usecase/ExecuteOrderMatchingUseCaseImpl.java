package shop.shportfolio.trading.application.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.ports.input.ExecuteOrderMatchingUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.util.List;

@Slf4j
@Component
public class ExecuteOrderMatchingUseCaseImpl implements ExecuteOrderMatchingUseCase {

    private final OrderBookManager orderBookManager;
    private final TradeKafkaPublisher tradeKafkaPublisher;
    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final UserBalanceKafkaPublisher userBalanceKafkaPublisher;

    public ExecuteOrderMatchingUseCaseImpl(
            OrderBookManager orderBookManager,
            TradeKafkaPublisher tradeKafkaPublisher,
            List<OrderMatchingStrategy<? extends Order>> strategies,
            UserBalanceKafkaPublisher userBalanceKafkaPublisher) {
        this.orderBookManager = orderBookManager;
        this.tradeKafkaPublisher = tradeKafkaPublisher;
        this.strategies = strategies;
        this.userBalanceKafkaPublisher = userBalanceKafkaPublisher;
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {
        execute(marketOrder);
    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {
        execute(limitOrder);
    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {
        execute(reservationOrder);
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

    private <T extends Order> void execute(T order) {
        log.info("[{}] Received {} order: userId={}, marketId={}, orderPrice={}",
                order.getId().getValue(),
                order.getOrderType(),
                order.getUserId().getValue(),
                order.getMarketId().getValue(),
                order instanceof LimitOrder ? ((LimitOrder) order).getOrderPrice().getValue() : "MarketOrder");

        OrderBook orderBook = this.extractOrderBook(order.getMarketId().getValue());

        OrderMatchingStrategy<T> strategy = findStrategy(order);
        log.info("[{}] Selected matching strategy: {}", order.getId().getValue(), strategy.getClass().getSimpleName());

        TradeMatchingContext matchingContext = strategy.match(orderBook, order);

        log.info("[{}] Matching finished: executed trades={}", order.getId().getValue(),
                matchingContext.getTradingRecordedEvents().size());
        matchingContext.getTradingRecordedEvents().forEach(trade ->
                log.info("[{}] Trade executed: {}", order.getId().getValue(), trade.getDomainType().getTransactionType()));

        userBalanceKafkaPublisher.publish(matchingContext.getUserBalanceUpdatedEvent());
        matchingContext.getTradingRecordedEvents().forEach(tradeKafkaPublisher::publish);
        log.info("[{}] UserBalanceUpdatedEvent published: userId={}, type={}",
                order.getId().getValue(),
                matchingContext.getUserBalanceUpdatedEvent().getDomainType().getUserId().getValue(),
                matchingContext.getUserBalanceUpdatedEvent().getMessageType());
    }
}
