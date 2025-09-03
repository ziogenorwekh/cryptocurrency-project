package shop.shportfolio.matching.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;

public interface MatchedPublisher extends DomainEventPublisher<PredictedTradeCreatedEvent> {
}
