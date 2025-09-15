package shop.shportfolio.portfolio.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

@Component
public class DepositKafkaPublisher implements DepositPublisher {

    private final KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher;
    private final PortfolioMessageMapper portfolioMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public DepositKafkaPublisher(KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher,
                                 PortfolioMessageMapper portfolioMessageMapper,
                                 KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(DepositCreatedEvent domainEvent) {
        String transactionId = domainEvent.getDomainType().getId().getValue().toString();
        DepositWithdrawalAvroModel avroModel = portfolioMessageMapper
                .depositWithdrawalToDepositWithdrawalAvroModel(domainEvent.getDomainType());
        kafkaPublisher.send(kafkaTopicData.getDepositWithdrawalTopic(), transactionId, avroModel);
    }
}
