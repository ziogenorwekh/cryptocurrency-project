package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface ReservationOrderJpaRepository extends JpaRepository<ReservationOrderEntity, String> {
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderIdAndUserId(String orderId, UUID userId);

    @Query("select r from ReservationOrderEntity r where r.orderId = ?1")
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderId(String orderId);

}
