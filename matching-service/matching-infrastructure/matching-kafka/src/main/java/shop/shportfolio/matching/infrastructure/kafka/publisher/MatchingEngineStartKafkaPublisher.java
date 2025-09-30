package shop.shportfolio.matching.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MatchingEngineStartAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.common.kafka.data.KafkaTopicData;

import java.util.UUID;

@Slf4j
@Component
public class MatchingEngineStartKafkaPublisher implements DomainEventPublisher<DomainEvent> {

    private final KafkaTemplate<String, MatchingEngineStartAvroModel> kafkaTemplate;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public MatchingEngineStartKafkaPublisher(KafkaTemplate<String,
            MatchingEngineStartAvroModel> kafkaTemplate, KafkaTopicData kafkaTopicData) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(DomainEvent domainEvent) {
        UUID randomId = UUID.randomUUID();
        MatchingEngineStartAvroModel avroModel = MatchingEngineStartAvroModel.newBuilder()
                .setRandomId(randomId.toString())
                .setMessageType(MessageType.CREATE).build();
        log.info("Publishing matching engine start event: {}", avroModel);
        kafkaTemplate.send(kafkaTopicData.getMatchingStartCommandTopic(), randomId.toString(), avroModel);
    }
}
