package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.database.repository.MessageEventJpaRepository;

@Slf4j
@Component
public class OutBoxRetryUpdater {

    private final MessageEventJpaRepository messageEventJpaRepository;

    public OutBoxRetryUpdater(MessageEventJpaRepository messageEventJpaRepository) {
        this.messageEventJpaRepository = messageEventJpaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusAndRetryCount(MessageEventEntity entity) {

        messageEventJpaRepository.save(entity);
    }

    public void deleteOutbox(MessageEventEntity entity) {
        messageEventJpaRepository.delete(entity);
    }

    public void updateSent(MessageEventEntity entity) {
        entity.sent();
        messageEventJpaRepository.save(entity);
    }
}
