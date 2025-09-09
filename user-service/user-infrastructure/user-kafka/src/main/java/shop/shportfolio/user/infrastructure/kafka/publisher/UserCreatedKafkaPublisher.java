package shop.shportfolio.user.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.user.application.ports.output.kafka.UserCreatedPublisher;
import shop.shportfolio.user.domain.event.UserCreatedEvent;

@Component
public class UserCreatedKafkaPublisher implements UserCreatedPublisher {

    private final KafkaPublisher<String, UserIdAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public UserCreatedKafkaPublisher(KafkaPublisher<String, UserIdAvroModel> kafkaPublisher,
                                     KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(UserCreatedEvent domainEvent) {
        String userId = domainEvent.getDomainType().getValue().toString();
        UserIdAvroModel avroModel = UserIdAvroModel.newBuilder()
                .setUserId(userId)
                .setMessageType(toMessageType(domainEvent.getMessageType()))
                .build();
        kafkaPublisher.send(kafkaTopicData.getUserTopic(), userId, avroModel);
    }

    private MessageType toMessageType(shop.shportfolio.common.domain.valueobject.MessageType messageType) {
        return switch (messageType) {
            case CREATE -> MessageType.CREATE;
            case DELETE -> MessageType.DELETE;
            case FAIL -> MessageType.FAIL;
            case REJECT -> MessageType.REJECT;
            case UPDATE -> MessageType.UPDATE;
            case NO_DEF -> MessageType.NO_DEF;
        };
    }
}
