package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.ReservationOrderAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.ReservationOrderCreatedPublisher;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class ReservationOrderKafkaPublisher implements ReservationOrderCreatedPublisher {

    private final KafkaPublisher<String, ReservationOrderAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public ReservationOrderKafkaPublisher(KafkaPublisher<String, ReservationOrderAvroModel> kafkaPublisher,
                                          TradingMessageMapper tradingMessageMapper,
                                          KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(ReservationOrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        ReservationOrderAvroModel reservationOrderAvroModel = tradingMessageMapper
                .toReservationOrderAvroModel(domainEvent);
        log.info("publish reservationOrder -> {}", reservationOrderAvroModel);
        kafkaPublisher.send(kafkaTopicData.getReservationOrderCommandTopic(), orderId, reservationOrderAvroModel);
    }
}
