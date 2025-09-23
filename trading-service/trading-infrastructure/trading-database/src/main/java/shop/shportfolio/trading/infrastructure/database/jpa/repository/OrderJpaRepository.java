package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.OrderEntity;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {

    List<OrderEntity> findOrderEntitiesByUserIdAndMarketId(UUID userId, String marketId);

    List<OrderEntity> findAllByOrderByCreatedAtAsc();

    @Query("select o from OrderEntity o where o.orderStatus in ?1 order by o.createdAt")
    List<OrderEntity> findOrderEntitiesByOrderStatusInOrderByCreatedAtAsc(List<OrderStatus> orderStatus);
}
