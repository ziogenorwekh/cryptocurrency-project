package shop.shportfolio.matching.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.matching.domain.entity.MatchingOrderCancel;
import shop.shportfolio.matching.domain.event.MatchingOrderCancelDeletedEvent;

public interface OrderCancelledPublisher extends DomainEventPublisher<MatchingOrderCancelDeletedEvent> {
}
