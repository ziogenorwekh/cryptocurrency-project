package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface ReservationOrderJpaRepository extends JpaRepository<ReservationOrderEntity, String> {
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderIdAndUserId(String orderId, UUID userId);

    @Query("select r from ReservationOrderEntity r where r.orderId = ?1")
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderId(String orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationOrderEntity r WHERE r.orderId = :orderId")
    Optional<ReservationOrderEntity> findReservationOrderEntityByOrderIdForUpdate(@Param("orderId") String orderId);
}
