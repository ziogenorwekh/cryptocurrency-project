package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.TradeAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.TradePublisher;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class TradeKafkaPublisher implements TradePublisher {

    private final KafkaPublisher<String, TradeAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public TradeKafkaPublisher(KafkaPublisher<String, TradeAvroModel> kafkaPublisher,
                               TradingMessageMapper tradingMessageMapper,
                               KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(TradeCreatedEvent domainEvent) {
        String tradeId = domainEvent.getDomainType().getId().getValue().toString();
        TradeAvroModel tradeAvroModel = tradingMessageMapper.tradeToTradeAvroModel(
                domainEvent.getDomainType(),domainEvent.getMessageType());
        log.info("publish trade -> {}", tradeAvroModel);
        kafkaPublisher.send(kafkaTopicData.getTradeCommandTopic(), tradeId, tradeAvroModel);
    }
}
