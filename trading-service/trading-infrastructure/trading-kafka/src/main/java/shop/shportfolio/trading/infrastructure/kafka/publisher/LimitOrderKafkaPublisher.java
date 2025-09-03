package shop.shportfolio.trading.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.LimitOrderAvroModel;
import shop.shportfolio.common.avro.TradeAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.LimitOrderPublisher;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Component
public class LimitOrderKafkaPublisher implements LimitOrderPublisher {

    private final KafkaPublisher<String, LimitOrderAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public LimitOrderKafkaPublisher(KafkaPublisher<String, LimitOrderAvroModel> kafkaPublisher,
                                    TradingMessageMapper tradingMessageMapper,
                                    KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }


    @Override
    public void publish(LimitOrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        LimitOrderAvroModel limitOrderAvroModel = tradingMessageMapper.toLimitOrderAvroModel(domainEvent);
        kafkaPublisher.send(kafkaTopicData.getLimitOrderTopic(), orderId, limitOrderAvroModel);
    }
}
