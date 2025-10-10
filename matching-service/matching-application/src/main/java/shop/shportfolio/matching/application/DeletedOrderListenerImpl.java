package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.order.OrderCancelKafkaResponse;
import shop.shportfolio.matching.application.exception.MatchingApplicationException;
import shop.shportfolio.matching.application.ports.output.repository.ExternalOrderBookStore;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;
import shop.shportfolio.matching.application.ports.input.kafka.DeletedOrderListener;
import shop.shportfolio.matching.application.ports.output.kafka.OrderCancelledPublisher;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.event.MatchingOrderCancelDeletedEvent;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.Optional;

@Slf4j
@Component
public class DeletedOrderListenerImpl implements DeletedOrderListener {

    private final OrderStore orderStore;
    private final MatchingDomainService matchingDomainService;
    private final OrderCancelledPublisher orderCancelledPublisher;
    private final ExternalOrderBookStore externalOrderBookStore; // 추가

    @Autowired
    public DeletedOrderListenerImpl(OrderStore orderStore,
                                    MatchingDomainService matchingDomainService,
                                    OrderCancelledPublisher orderCancelledPublisher,
                                    ExternalOrderBookStore externalOrderBookStore) { // 주입
        this.orderStore = orderStore;
        this.matchingDomainService = matchingDomainService;
        this.orderCancelledPublisher = orderCancelledPublisher;
        this.externalOrderBookStore = externalOrderBookStore;
    }

    @Override
    public void deleteLimitOrder(OrderCancelKafkaResponse response) {
        MatchingOrderCancelDeletedEvent deletedEvent;
        try {
            Optional<LimitOrder> limitOrder = orderStore.findLimitOrderById(
                    response.getOrderId(),response.getMarketId());
            if (limitOrder.isPresent()) {
                Object marketLock = externalOrderBookStore.getLock(limitOrder.get().getMarketId().getValue());
                synchronized (marketLock) {
                    orderStore.removeLimitOrder(limitOrder.get());
                }
                deletedEvent = matchingDomainService.successfulDeleteOrder(limitOrder.get());
                log.info("delete limit order is -> {}", deletedEvent.getDomainType().toString());
                orderCancelledPublisher.publish(deletedEvent);
            } else {
                throw new MatchingApplicationException("LimitOrder not found");
            }
        } catch (Exception e) {
            deletedEvent = matchingDomainService.failedDeleteOrder(response.getOrderId(),
                    response.getUserId(), response.getOrderType(), response.getOrderStatus());
            orderCancelledPublisher.publish(deletedEvent);
        }
    }

    @Override
    public void deleteReservationOrder(OrderCancelKafkaResponse response) {
        MatchingOrderCancelDeletedEvent deletedEvent;
        try {
            Optional<ReservationOrder> reservationOrder = orderStore.findReservationOrderById(response.getOrderId()
            ,response.getMarketId());
            if (reservationOrder.isPresent()) {
                Object marketLock = externalOrderBookStore.getLock(reservationOrder.get()
                        .getMarketId().getValue());
                synchronized (marketLock) {
                    orderStore.removeReservationOrder(reservationOrder.get());
                }
                deletedEvent = matchingDomainService.successfulDeleteOrder(reservationOrder.get());
                log.info("delete reservation is -> {}", deletedEvent.getDomainType().toString());
                orderCancelledPublisher.publish(deletedEvent);
            } else {
                throw new MatchingApplicationException("ReservationOrder not found");
            }
        } catch (Exception e) {
            deletedEvent = matchingDomainService.failedDeleteOrder(response.getOrderId(),
                    response.getUserId(), response.getOrderType(), response.getOrderStatus());
            orderCancelledPublisher.publish(deletedEvent);
        }
    }
}
