package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;

import java.util.List;

public interface MessageEventJpaRepository extends JpaRepository<MessageEventEntity, Long> {

    List<MessageEventEntity> findByOutBoxStatusOrderByCreatedAtAsc(OutBoxStatus outBoxStatus);
}
