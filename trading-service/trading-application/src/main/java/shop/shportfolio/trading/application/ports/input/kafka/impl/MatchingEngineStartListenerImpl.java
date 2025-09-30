package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.application.dto.context.OrderContext;
import shop.shportfolio.trading.application.ports.input.kafka.MatchingEngineStartListener;
import shop.shportfolio.trading.application.ports.output.kafka.LimitOrderCreatedPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.MarketOrderCreatedPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.ReservationOrderCreatedPublisher;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
@Slf4j
@Component
public class MatchingEngineStartListenerImpl implements MatchingEngineStartListener {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final LimitOrderCreatedPublisher limitOrderCreatedPublisher;
    private final MarketOrderCreatedPublisher marketOrderCreatedPublisher;
    private final ReservationOrderCreatedPublisher reservationOrderCreatedPublisher;

    public MatchingEngineStartListenerImpl(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                           LimitOrderCreatedPublisher limitOrderCreatedPublisher,
                                           MarketOrderCreatedPublisher marketOrderCreatedPublisher,
                                           ReservationOrderCreatedPublisher reservationOrderCreatedPublisher) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.limitOrderCreatedPublisher = limitOrderCreatedPublisher;
        this.marketOrderCreatedPublisher = marketOrderCreatedPublisher;
        this.reservationOrderCreatedPublisher = reservationOrderCreatedPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public void sendOpenOrdersToMatchingEngine() {
        List<OrderStatus> openStatus = List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED);
        OrderContext context = tradingOrderRepositoryPort.findOrdersByOrderStatusIn(openStatus);
        context.getLimitOrders().forEach(order -> {
            log.info("send open limit order to matching engine -> {}", order.getId().getValue());
            LimitOrderCreatedEvent event = new LimitOrderCreatedEvent(order,
                    MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
            limitOrderCreatedPublisher.publish(event);
        });
        context.getMarketOrders().forEach(order -> {
            log.info("send open market order to matching engine -> {}", order.getId().getValue());
            marketOrderCreatedPublisher.publish(
                    new MarketOrderCreatedEvent(order, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC))
            );
        });
        context.getReservationOrders().forEach(order -> {
            log.info("send open reservation order to matching engine -> {}", order.getId().getValue());
            reservationOrderCreatedPublisher.publish(
                    new ReservationOrderCreatedEvent(order, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC))
            );
        });
    }
}
