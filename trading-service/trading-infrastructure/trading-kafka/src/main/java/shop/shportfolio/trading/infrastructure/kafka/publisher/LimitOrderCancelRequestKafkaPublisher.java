package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CancelOrderAvroModel;
import shop.shportfolio.common.avro.LimitOrderAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.LimitOrderCancelledPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.LimitOrderCreatedPublisher;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class LimitOrderCancelRequestKafkaPublisher implements LimitOrderCancelledPublisher {

    private final KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    public LimitOrderCancelRequestKafkaPublisher(KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher,
                                                 TradingMessageMapper tradingMessageMapper,
                                                 KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(LimitOrderCanceledEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        CancelOrderAvroModel avroModel = tradingMessageMapper.toCancelOrderAvroModel(domainEvent.getDomainType(),
                domainEvent.getMessageType());
        log.info("Publishing cancel request limit order avro model for orderId: {}", orderId);
        kafkaPublisher.send(kafkaTopicData.getOrderTopic(),orderId, avroModel);

    }
}
