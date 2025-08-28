package shop.shportfolio.matching.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

public interface MatchedKafkaPublisher extends DomainEventPublisher<TradeCreatedEvent> {
}
