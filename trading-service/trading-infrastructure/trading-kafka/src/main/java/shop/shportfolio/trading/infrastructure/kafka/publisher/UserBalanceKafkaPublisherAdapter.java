package shop.shportfolio.trading.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Component
public class UserBalanceKafkaPublisherAdapter implements UserBalanceKafkaPublisher {

    private final KafkaPublisher<String, UserBalanceAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public UserBalanceKafkaPublisherAdapter(KafkaPublisher<String, UserBalanceAvroModel> kafkaPublisher,
                                            TradingMessageMapper tradingMessageMapper,
                                            KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(UserBalanceUpdatedEvent domainEvent) {
        String userBalanceId = domainEvent.getDomainType().getId().toString();
        UserBalanceAvroModel userBalanceAvroModel = tradingMessageMapper
                .userBalanceToUserBalanceAvroModel(
                        domainEvent.getDomainType(),
                        domainEvent.getMessageType());
        kafkaPublisher.send(kafkaTopicData.getUserBalanceTopic(),
                userBalanceId, userBalanceAvroModel);
    }
}
