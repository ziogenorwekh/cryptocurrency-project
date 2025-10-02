package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioOutBoxMapper;

@Slf4j
@Component
public class OutBoxMessageProcessor {

    private final PortfolioOutBoxMapper portfolioOutBoxMapper;
    private final PortfolioMessageMapper portfolioMessageMapper;
    private final KafkaPublisher<String, SpecificRecordBase> kafkaPublisher;
    private final OutBoxRetryUpdater outBoxRetryUpdater;
    @Autowired
    public OutBoxMessageProcessor(PortfolioOutBoxMapper portfolioOutBoxMapper,
                                  PortfolioMessageMapper portfolioMessageMapper,
                                  KafkaPublisher<String, SpecificRecordBase> kafkaPublisher,
                                  OutBoxRetryUpdater outBoxRetryUpdater) {
        this.portfolioOutBoxMapper = portfolioOutBoxMapper;
        this.portfolioMessageMapper = portfolioMessageMapper;
        this.kafkaPublisher = kafkaPublisher;
        this.outBoxRetryUpdater = outBoxRetryUpdater;
    }

    @Transactional
    public void process(MessageEventEntity entity) {
        final int MAX_RETRIES = 3;
        try {
            if (entity.getAggregateType().equals(DepositWithdrawal.class.getSimpleName())) {
                DepositWithdrawal depositWithdrawal = portfolioOutBoxMapper
                        .deserializeDepositWithdrawal(entity.getPayload());
                DepositWithdrawalAvroModel depositWithdrawalAvroModel = portfolioMessageMapper
                        .depositWithdrawalToDepositWithdrawalAvroModel(depositWithdrawal);
                kafkaPublisher.send(entity.getTopicName(), entity.getKafkaKey(), depositWithdrawalAvroModel);
                log.info("successful sent kafka message");
//                outBoxRetryUpdater.updateSent(entity);
                outBoxRetryUpdater.deleteOutbox(entity);
            }
        } catch (Exception e) {
            entity.incrementRetryCount();
            outBoxRetryUpdater.updateStatusAndRetryCount(entity);
            log.warn("Attempt {} failed for key: {}", entity.getRetryCount(), entity.getKafkaKey());
            if (entity.getRetryCount() >= MAX_RETRIES) {
                entity.failed();

                log.error("Permanent failure after {} retries for key: {}", MAX_RETRIES, entity.getKafkaKey(), e);
            } else {
                log.warn("Will retry sending message for key: {}", entity.getKafkaKey());
            }
        }
    }
}
