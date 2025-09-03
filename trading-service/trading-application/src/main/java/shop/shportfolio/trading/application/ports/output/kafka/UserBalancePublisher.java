package shop.shportfolio.trading.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

public interface UserBalancePublisher extends DomainEventPublisher<UserBalanceUpdatedEvent> {
}
