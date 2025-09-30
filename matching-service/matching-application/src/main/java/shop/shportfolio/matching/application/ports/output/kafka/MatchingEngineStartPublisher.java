package shop.shportfolio.matching.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.event.DomainEventPublisher;

public interface MatchingEngineStartPublisher extends DomainEventPublisher<DomainEvent> {
}
