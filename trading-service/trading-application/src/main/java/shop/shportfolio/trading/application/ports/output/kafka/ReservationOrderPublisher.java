package shop.shportfolio.trading.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;

public interface ReservationOrderPublisher extends DomainEventPublisher<ReservationOrderCreatedEvent> {
}
