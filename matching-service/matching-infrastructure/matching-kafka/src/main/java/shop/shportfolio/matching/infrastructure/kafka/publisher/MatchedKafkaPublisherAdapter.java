package shop.shportfolio.matching.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.PredictedTradeAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedKafkaPublisher;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;

@Component
public class MatchedKafkaPublisherAdapter implements MatchedKafkaPublisher {

    private final KafkaPublisher<String, PredictedTradeAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public MatchedKafkaPublisherAdapter(KafkaPublisher<String, PredictedTradeAvroModel> kafkaPublisher,
                                        KafkaTopicData kafkaTopicData,
                                        MatchingMessageMapper matchingMessageMapper) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    public void publish(PredictedTradeCreatedEvent domainEvent) {
        PredictedTradeAvroModel predictedTradeAvroModel = matchingMessageMapper
                .predictedTradeToPredictedTradeAvroModel(domainEvent);
        kafkaPublisher.send(kafkaTopicData.getPredicatedTradeTopic(),
                domainEvent.getDomainType().getId().getValue().toString(),
                predictedTradeAvroModel);
    }
}
