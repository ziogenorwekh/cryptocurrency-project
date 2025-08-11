package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface ReservationOrderJpaRepository extends JpaRepository<ReservationOrderEntity, String> {
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderIdAndUserId(String orderId, UUID userId);
}
