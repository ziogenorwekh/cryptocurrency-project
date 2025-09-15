package shop.shportfolio.portfolio.application.port.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.portfolio.domain.event.CryptoUpdatedEvent;

public interface CryptoAmountPublisher extends DomainEventPublisher<CryptoUpdatedEvent> {
}
