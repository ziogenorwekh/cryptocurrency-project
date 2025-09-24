package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalancePublisher;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class UserBalanceKafkaPublisher implements UserBalancePublisher {

    private final KafkaPublisher<String, UserBalanceAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public UserBalanceKafkaPublisher(KafkaPublisher<String, UserBalanceAvroModel> kafkaPublisher,
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
        log.info("publish userBalance -> {}", userBalanceAvroModel);
        kafkaPublisher.send(kafkaTopicData.getUserBalanceCommandTopic(),
                userBalanceId, userBalanceAvroModel);
    }
}
