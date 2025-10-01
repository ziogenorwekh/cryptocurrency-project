package shop.shportfolio.portfolio.infrastructure.kafka.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;
import shop.shportfolio.portfolio.infrastructure.database.repository.MessageEventJpaRepository;

import java.util.List;

@Component
public class OutBoxScheduler {

    private final MessageEventJpaRepository messageEventJpaRepository;

    private final DepositPublisher depositPublisher;

    @Autowired
    public OutBoxScheduler(MessageEventJpaRepository messageEventJpaRepository,
                           DepositPublisher depositPublisher) {
        this.messageEventJpaRepository = messageEventJpaRepository;
        this.depositPublisher = depositPublisher;
    }

    @Scheduled(fixedRate = 1000)
    public void schedulingOutBox() {
        try {
            List<MessageEventEntity> entities = messageEventJpaRepository
                    .findByOutBoxStatusOrderByCreatedAtAsc(OutBoxStatus.NEW);

        } catch (Exception e) {

        }
    }
}
