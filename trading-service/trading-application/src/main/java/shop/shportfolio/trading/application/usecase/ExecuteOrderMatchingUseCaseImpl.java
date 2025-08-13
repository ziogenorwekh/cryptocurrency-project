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
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.util.List;

@Slf4j
@Component
public class ExecuteOrderMatchingUseCaseImpl implements ExecuteOrderMatchingUseCase {

    private final OrderBookManager orderBookManager;
    private final TradeKafkaPublisher tradeKafkaPublisher;
    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    public ExecuteOrderMatchingUseCaseImpl(OrderBookManager orderBookManager, TradeKafkaPublisher tradeKafkaPublisher,
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
        MarketItem marketItem = orderBookManager.findMarketItemById(marketId);
        // 여기서 Trade 결과값을 가져와서 orderBook에 반영해야 됌
        return orderBookManager.loadAdjustedOrderBook(marketItem.getId().getValue(),
                marketItem.getTickPrice().getValue());
    }

    private <T extends Order> void execute(T order) {
        OrderBook orderBook = this.extractOrderBook(order.getMarketId().getValue());
        OrderMatchingStrategy<T> strategy = findStrategy(order);
        TradeMatchingContext matchingContext = strategy.match(orderBook, order);
        log.info("tradingRecordedEvents's size is -> {}", matchingContext.getTradingRecordedEvents().size());
        matchingContext.getTradingRecordedEvents().forEach(tradeKafkaPublisher::publish);
        userBalanceKafkaPublisher.publish(matchingContext.getUserBalanceUpdatedEvent());

    }
}
