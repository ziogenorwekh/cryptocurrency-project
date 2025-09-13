package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.order.OrderCancelKafkaResponse;
import shop.shportfolio.matching.application.exception.MatchingApplicationException;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
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

    private final OrderMemoryStore orderMemoryStore;
    private final MatchingDomainService matchingDomainService;
    private final OrderCancelledPublisher orderCancelledPublisher;
    private final ExternalOrderBookMemoryStore externalOrderBookMemoryStore; // 추가

    @Autowired
    public DeletedOrderListenerImpl(OrderMemoryStore orderMemoryStore,
                                    MatchingDomainService matchingDomainService,
                                    OrderCancelledPublisher orderCancelledPublisher,
                                    ExternalOrderBookMemoryStore externalOrderBookMemoryStore) { // 주입
        this.orderMemoryStore = orderMemoryStore;
        this.matchingDomainService = matchingDomainService;
        this.orderCancelledPublisher = orderCancelledPublisher;
        this.externalOrderBookMemoryStore = externalOrderBookMemoryStore;
    }

    @Override
    public void deleteLimitOrder(OrderCancelKafkaResponse response) {
        MatchingOrderCancelDeletedEvent deletedEvent;
        try {
            Optional<LimitOrder> limitOrder = orderMemoryStore.findLimitOrderById(response.getOrderId());
            if (limitOrder.isPresent()) {
                Object marketLock = externalOrderBookMemoryStore.getLock(limitOrder.get().getMarketId().getValue());
                synchronized (marketLock) {
                    orderMemoryStore.deleteLimitOrder(limitOrder.get());
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
            ReservationOrder reservationOrder = orderMemoryStore.findReservationOrderById(response.getOrderId());
            if (reservationOrder != null) {
                Object marketLock = externalOrderBookMemoryStore.getLock(reservationOrder.getMarketId().getValue());
                synchronized (marketLock) {
                    orderMemoryStore.deleteReservationOrder(reservationOrder);
                }
                deletedEvent = matchingDomainService.successfulDeleteOrder(reservationOrder);
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
