package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon.CouponInfoEntity;

import java.util.UUID;

public interface CouponInfoJpaRepository extends JpaRepository<CouponInfoEntity, UUID> {
}
