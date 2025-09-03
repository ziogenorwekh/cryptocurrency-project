package shop.shportfolio.user.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.user.domain.event.UserCreatedEvent;

public interface UserCreatedPublisher extends DomainEventPublisher<UserCreatedEvent> {
}
