package shop.shportfolio.portfolio.application.port.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;

public interface DepositPublisher extends DomainEventPublisher<DepositCreatedEvent> {
}
