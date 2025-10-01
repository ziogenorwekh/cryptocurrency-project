package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.common.kafka.data.KafkaTopicData;
import shop.shportfolio.portfolio.application.port.output.kafka.OutBoxRecorder;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.database.mapper.PortfolioMessageEventMapper;
import shop.shportfolio.portfolio.infrastructure.database.repository.MessageEventJpaRepository;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioOutBoxMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class OutBoxKafkaRecorder implements OutBoxRecorder {

    private final PortfolioMessageEventMapper portfolioMessageEventMapper;
    private final PortfolioOutBoxMapper portfolioOutBoxMapper;
    private final MessageEventJpaRepository messageEventJpaRepository;
    private final KafkaTopicData kafkaTopicData;
    public OutBoxKafkaRecorder(PortfolioMessageEventMapper portfolioMessageEventMapper,
                               PortfolioOutBoxMapper portfolioOutBoxMapper,
                               MessageEventJpaRepository messageEventJpaRepository,
                               KafkaTopicData kafkaTopicData) {
        this.portfolioMessageEventMapper = portfolioMessageEventMapper;
        this.portfolioOutBoxMapper = portfolioOutBoxMapper;
        this.messageEventJpaRepository = messageEventJpaRepository;
        this.kafkaTopicData = kafkaTopicData;
    }

    @Override
    public void saveDepositEvent(DepositCreatedEvent domainEvent) {
        String payload = portfolioOutBoxMapper.serializeEvent(domainEvent.getDomainType());
        String aggregateId = domainEvent.getDomainType().getId().getValue().toString();

        MessageEventEntity entity = portfolioMessageEventMapper.toMessageEventEntity(
                kafkaTopicData.getDepositWithdrawalCommandTopic(),
                aggregateId,
                payload,
                LocalDateTime.now(ZoneOffset.UTC),
                OutBoxStatus.NEW,
                aggregateId,
                domainEvent.getDomainType().getClass().getSimpleName());
        messageEventJpaRepository.save(entity);
    }

    @Override
    public void saveWithdrawalEvent(WithdrawalCreatedEvent domainEvent) {
        String payload = portfolioOutBoxMapper.serializeEvent(domainEvent.getDomainType());
        String aggregateId = domainEvent.getDomainType().getId().getValue().toString();

        MessageEventEntity entity = portfolioMessageEventMapper.toMessageEventEntity(
                kafkaTopicData.getDepositWithdrawalCommandTopic(),
                aggregateId,
                payload,
                LocalDateTime.now(ZoneOffset.UTC),
                OutBoxStatus.NEW,
                aggregateId,
                domainEvent.getDomainType().getClass().getSimpleName());
        messageEventJpaRepository.save(entity);
    }
}
