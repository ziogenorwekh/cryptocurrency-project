package shop.shportfolio.trading.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.TradingDepositWithdrawalPublisher;
import shop.shportfolio.trading.domain.event.DepositWithdrawalUpdatedEvent;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

@Slf4j
@Component
public class TradingDepositWithdrawalKafkaPublisher implements TradingDepositWithdrawalPublisher {

    private final KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher;
    private final TradingMessageMapper tradingMessageMapper;
    private final KafkaTopicData kafkaTopicData;

    public TradingDepositWithdrawalKafkaPublisher(KafkaPublisher<String, DepositWithdrawalAvroModel> kafkaPublisher, TradingMessageMapper tradingMessageMapper, KafkaTopicData kafkaTopicData) {
        this.kafkaPublisher = kafkaPublisher;
        this.tradingMessageMapper = tradingMessageMapper;
        this.kafkaTopicData = kafkaTopicData;
    }


    @Override
    public void publish(DepositWithdrawalUpdatedEvent domainEvent) {
        DepositWithdrawalAvroModel depositWithdrawalAvroModel = tradingMessageMapper
                .toDepositWithdrawalAvroModel(domainEvent);
        kafkaPublisher.send(kafkaTopicData.getDepositWithdrawalEventTopic(),
                domainEvent.getDomainType().getId().toString(), depositWithdrawalAvroModel);
        log.info("Published deposit-withdrawal-updated event -> {}", depositWithdrawalAvroModel.toString());
    }
}
