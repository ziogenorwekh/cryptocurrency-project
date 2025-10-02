package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.common.kafka.publisher.KafkaPublisher;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.database.repository.MessageEventJpaRepository;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioOutBoxMapper;

import java.util.List;

@Slf4j
@Component
public class OutBoxRelay {

    private final MessageEventJpaRepository messageEventJpaRepository;
    private final OutBoxMessageProcessor outBoxMessageProcessor;
    @Autowired
    public OutBoxRelay(MessageEventJpaRepository messageEventJpaRepository,
                       OutBoxMessageProcessor outBoxMessageProcessor) {
        this.messageEventJpaRepository = messageEventJpaRepository;
        this.outBoxMessageProcessor = outBoxMessageProcessor;
    }


    @Scheduled(fixedRate = 1000)
    public void schedulingOutBox() {
        List<MessageEventEntity> entities = messageEventJpaRepository
                .findByOutBoxStatusOrderByCreatedAtAsc(OutBoxStatus.NEW);
        entities.forEach(outBoxMessageProcessor::process);
    }


}
