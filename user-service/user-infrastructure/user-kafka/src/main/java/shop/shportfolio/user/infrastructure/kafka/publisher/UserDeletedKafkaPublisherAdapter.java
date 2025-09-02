package shop.shportfolio.user.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.UserIdAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.user.application.ports.output.kafka.UserDeletedKafkaPublisher;
import shop.shportfolio.user.domain.event.UserDeletedEvent;

@Component
public class UserDeletedKafkaPublisherAdapter implements UserDeletedKafkaPublisher {

    private final KafkaPublisher<String, UserIdAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public UserDeletedKafkaPublisherAdapter(KafkaPublisher<String, UserIdAvroModel> kafkaPublisher,
                                            KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(UserDeletedEvent domainEvent) {
        String userId = domainEvent.getDomainType().getValue().toString();
        UserIdAvroModel avroModel = UserIdAvroModel.newBuilder().setUserId(userId).build();
        kafkaPublisher.send(kafkaTopicData.getUserTopic(), userId, avroModel);
    }
}
