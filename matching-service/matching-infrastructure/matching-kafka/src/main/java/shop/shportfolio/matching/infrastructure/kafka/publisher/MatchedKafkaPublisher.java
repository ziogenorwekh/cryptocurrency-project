package shop.shportfolio.matching.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.PredicatedTradeAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedPublisher;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;

@Slf4j
@Component
public class MatchedKafkaPublisher implements MatchedPublisher {

    private final KafkaPublisher<String, PredicatedTradeAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public MatchedKafkaPublisher(KafkaPublisher<String, PredicatedTradeAvroModel> kafkaPublisher,
                                 KafkaTopicData kafkaTopicData,
                                 MatchingMessageMapper matchingMessageMapper) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    public void publish(PredictedTradeCreatedEvent domainEvent) {
        PredicatedTradeAvroModel predicatedTradeAvroModel = matchingMessageMapper
                .predictedTradeToPredictedTradeAvroModel(domainEvent);
        log.info("order matched publish -> {}", predicatedTradeAvroModel.toString());
        kafkaPublisher.send(kafkaTopicData.getPredicatedTradeTopic(),
                domainEvent.getDomainType().getId().getValue().toString(),
                predicatedTradeAvroModel);
    }
}
