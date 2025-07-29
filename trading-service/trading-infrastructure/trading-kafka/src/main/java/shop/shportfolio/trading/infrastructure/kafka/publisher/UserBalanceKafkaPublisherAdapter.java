package shop.shportfolio.trading.infrastructure.kafka.publisher;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

@Component
public class UserBalanceKafkaPublisherAdapter implements UserBalanceKafkaPublisher {


    @Override
    public void publish(UserBalanceUpdatedEvent domainEvent) {

    }
}
