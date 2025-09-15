package shop.shportfolio.portfolio.infrastructure.kafka.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

@Component
public class WithdrawalKafkaPublisher implements WithdrawalPublisher {

    private final KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher;
    private final PortfolioMessageMapper portfolioMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    @Autowired
    public WithdrawalKafkaPublisher(KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher,
                                    PortfolioMessageMapper portfolioMessageMapper,
                                    KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void publish(WithdrawalCreatedEvent domainEvent) {
        DepositWithdrawalAvroModel avroModel = portfolioMessageMapper
                .depositWithdrawalToDepositWithdrawalAvroModel(domainEvent.getDomainType());
        String transactionId = domainEvent.getDomainType().getId().toString();
        kafkaPublisher.send(kafkaTopicData.getDepositWithdrawalTopic(),
                transactionId, avroModel);
    }
}
