package shop.shportfolio.portfolio.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CryptoAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.CryptoAmountPublisher;
import shop.shportfolio.portfolio.domain.event.CryptoUpdatedEvent;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

@Slf4j
@Component
public class CryptoAmountKafkaPublisher implements CryptoAmountPublisher {

    private final KafkaPublisher<String, CryptoAvroModel> kafkaPublisher;
    private final PortfolioMessageMapper portfolioMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    public CryptoAmountKafkaPublisher(KafkaPublisher<String, CryptoAvroModel> kafkaPublisher,
                                      PortfolioMessageMapper portfolioMessageMapper,
                                      KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(CryptoUpdatedEvent domainEvent) {
        String key = domainEvent.getDomainType().getId().toString();
        CryptoAvroModel model = portfolioMessageMapper.toCryptoAvroModel(domainEvent.getDomainType(),
                domainEvent.getMessageType());
        log.info("crypto published -> {} ", model.toString());
        kafkaPublisher.send(kafkaTopicData.getCryptoCommandTopic(), key, model);
    }
}
