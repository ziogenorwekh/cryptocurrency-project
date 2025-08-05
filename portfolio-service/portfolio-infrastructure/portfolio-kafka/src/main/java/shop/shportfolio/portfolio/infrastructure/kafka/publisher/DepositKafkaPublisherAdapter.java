package shop.shportfolio.portfolio.infrastructure.kafka.publisher;

import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositKafkaPublisher;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;

@Component
public class DepositKafkaPublisherAdapter implements DepositKafkaPublisher {



    @Override
    public void publish(DepositCreatedEvent domainEvent) {

    }
}
