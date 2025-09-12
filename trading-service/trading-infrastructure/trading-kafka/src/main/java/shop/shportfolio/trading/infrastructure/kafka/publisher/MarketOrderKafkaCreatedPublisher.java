package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MarketOrderAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.MarketOrderCreatedPublisher;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class MarketOrderKafkaCreatedPublisher implements MarketOrderCreatedPublisher {

    private final KafkaPublisher<String, MarketOrderAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public MarketOrderKafkaCreatedPublisher(KafkaPublisher<String, MarketOrderAvroModel> kafkaPublisher,
                                            TradingMessageMapper tradingMessageMapper,
                                            KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(MarketOrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getDomainType().getId().getValue();
        MarketOrderAvroModel marketOrderAvroModel = tradingMessageMapper.toMarketOrderAvroModel(domainEvent);
        log.info("publish marketOrder -> {}", marketOrderAvroModel);
        kafkaPublisher.send(kafkaTopicData.getMarketOrderTopic(), orderId, marketOrderAvroModel);
    }
}
