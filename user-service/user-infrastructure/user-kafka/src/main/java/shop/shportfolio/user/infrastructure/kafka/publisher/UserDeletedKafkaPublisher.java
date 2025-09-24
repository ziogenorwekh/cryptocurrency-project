package shop.shportfolio.user.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.user.application.ports.output.kafka.UserDeletedPublisher;
import shop.shportfolio.user.domain.event.UserDeletedEvent;
import shop.shportfolio.user.infrastructure.kafka.publisher.mapper.UserMessageMapper;

@Component
public class UserDeletedKafkaPublisher implements UserDeletedPublisher {

    private final KafkaPublisher<String, UserIdAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;
    private final UserMessageMapper userMessageMapper;
    @Autowired
    public UserDeletedKafkaPublisher(KafkaPublisher<String, UserIdAvroModel> kafkaPublisher,
                                     KafkaTopicData kafkaTopicData, UserMessageMapper userMessageMapper) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
        this.userMessageMapper = userMessageMapper;
    }

    @Override
    public void publish(UserDeletedEvent domainEvent) {
        String userId = domainEvent.getDomainType().getValue().toString();
        UserIdAvroModel avroModel = userMessageMapper.toUserIdAvroModel(userId, domainEvent.getMessageType());
        kafkaPublisher.send(kafkaTopicData.getUserCommandTopic(), userId, avroModel);
    }
}
