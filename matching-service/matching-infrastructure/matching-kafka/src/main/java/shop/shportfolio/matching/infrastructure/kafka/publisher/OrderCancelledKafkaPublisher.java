package shop.shportfolio.matching.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CancelOrderAvroModel;
import shop.shportfolio.common.avro.PredicatedTradeAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.matching.application.ports.output.kafka.OrderCancelledPublisher;
import shop.shportfolio.matching.domain.event.MatchingOrderCancelDeletedEvent;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;

@Slf4j
@Component
public class OrderCancelledKafkaPublisher implements OrderCancelledPublisher {

    private final KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher;
    private final KafkaTopicData kafkaTopicData;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public OrderCancelledKafkaPublisher(KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher,
                                        KafkaTopicData kafkaTopicData,
                                        MatchingMessageMapper matchingMessageMapper) {
        this.kafkaPublisher = kafkaPublisher;
        this.kafkaTopicData = kafkaTopicData;
        this.matchingMessageMapper = matchingMessageMapper;
    }


    @Override
    public void publish(MatchingOrderCancelDeletedEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        CancelOrderAvroModel avroModel = matchingMessageMapper.toCancelOrderAvroModel(domainEvent.getDomainType(),
                domainEvent.getMessageType());
        kafkaPublisher.send(kafkaTopicData.getCancelOrderEventTopic(), orderId, avroModel);
    }
}
