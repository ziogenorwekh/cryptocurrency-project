package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CancelOrderAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.ReservationOrderCancelledPublisher;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class ReservationOrderCancelRequestKafkaPublisher implements ReservationOrderCancelledPublisher {
    private final KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public ReservationOrderCancelRequestKafkaPublisher(KafkaPublisher<String, CancelOrderAvroModel> kafkaPublisher,
                                                       TradingMessageMapper tradingMessageMapper,
                                                       KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(ReservationOrderCanceledEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        CancelOrderAvroModel avroModel = tradingMessageMapper.toCancelOrderAvroModel(
                domainEvent.getDomainType(), domainEvent.getMessageType());
        log.info("Publishing cancel request reservation order avro model for orderId: {}", orderId);
        kafkaPublisher.send(kafkaTopicData.getCancelOrderCommandTopic(),orderId, avroModel);
    }
}
