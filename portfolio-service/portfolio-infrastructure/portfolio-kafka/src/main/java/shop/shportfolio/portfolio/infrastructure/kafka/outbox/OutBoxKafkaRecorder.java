package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.portfolio.application.port.output.kafka.OutBoxRecorder;
import shop.shportfolio.portfolio.domain.event.CryptoUpdatedEvent;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.database.repository.MessageEventJpaRepository;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioOutBoxMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class OutBoxKafkaRecorder implements OutBoxRecorder {

    private final PortfolioOutBoxMapper portfolioOutBoxMapper;
    private final MessageEventJpaRepository messageEventJpaRepository;
    private final KafkaTopicData kafkaTopicData;

    public OutBoxKafkaRecorder(PortfolioOutBoxMapper portfolioOutBoxMapper,
                               MessageEventJpaRepository messageEventJpaRepository,
                               KafkaTopicData kafkaTopicData) {
        this.portfolioOutBoxMapper = portfolioOutBoxMapper;
        this.messageEventJpaRepository = messageEventJpaRepository;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void saveDepositEvent(DepositCreatedEvent domainEvent) {
        String payload = portfolioOutBoxMapper.serializeEvent(domainEvent.getDomainType());
        String aggregateId = domainEvent.getDomainType().getId().getValue().toString();

        MessageEventEntity entity = MessageEventEntity.builder()
                .kafkaKey(aggregateId)
                .topicName(kafkaTopicData.getDepositWithdrawalCommandTopic())
                .messageType(domainEvent.getMessageType())
                .payload(payload)
                .aggregateId(aggregateId)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .outBoxStatus(OutBoxStatus.NEW)
                .retryCount(0)
                .aggregateType(domainEvent.getDomainType().getClass().getSimpleName())
                .build();
        log.info("save deposit event -> {} , message -> {} , payload -> {} ",
                domainEvent.getDomainType().getClass().getSimpleName(),
                domainEvent.getMessageType(),
                payload);
        messageEventJpaRepository.save(entity);
    }

    @Override
    public void saveWithdrawalEvent(WithdrawalCreatedEvent domainEvent) {
        String payload = portfolioOutBoxMapper.serializeEvent(domainEvent.getDomainType());
        String aggregateId = domainEvent.getDomainType().getId().getValue().toString();

        MessageEventEntity entity = MessageEventEntity.builder()
                .kafkaKey(aggregateId)
                .topicName(kafkaTopicData.getDepositWithdrawalCommandTopic())
                .messageType(domainEvent.getMessageType())
                .payload(payload)
                .aggregateId(aggregateId)
                .retryCount(0)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .outBoxStatus(OutBoxStatus.NEW)
                .aggregateType(domainEvent.getDomainType().getClass().getSimpleName())
                .build();
        log.info("save deposit event -> {} , message -> {} , payload -> {} ",
                domainEvent.getDomainType().getClass().getSimpleName(),
                domainEvent.getMessageType(),
                payload);
        messageEventJpaRepository.save(entity);
    }

    @Override
    public void saveCryptoEvent(CryptoUpdatedEvent domainEvent) {
        String payload = portfolioOutBoxMapper.serializeEvent(domainEvent.getDomainType());
        String aggregateId = domainEvent.getDomainType().getId().getValue().toString();

        MessageEventEntity entity = MessageEventEntity.builder()
                .kafkaKey(aggregateId)
                .topicName(kafkaTopicData.getDepositWithdrawalCommandTopic())
                .messageType(domainEvent.getMessageType())
                .payload(payload)
                .aggregateId(aggregateId)
                .retryCount(0)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .outBoxStatus(OutBoxStatus.NEW)
                .aggregateType(domainEvent.getDomainType().getClass().getSimpleName())
                .build();
        log.info("save deposit event -> {} , message -> {} , payload -> {} ",
                domainEvent.getDomainType().getClass().getSimpleName(),
                domainEvent.getMessageType(),
                payload);
        messageEventJpaRepository.save(entity);
    }
}
