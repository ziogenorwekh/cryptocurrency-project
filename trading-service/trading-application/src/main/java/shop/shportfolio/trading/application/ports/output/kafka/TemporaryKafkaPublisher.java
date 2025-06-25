package shop.shportfolio.trading.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

public interface TemporaryKafkaPublisher extends DomainEventPublisher<TradingRecordedEvent> {
}
