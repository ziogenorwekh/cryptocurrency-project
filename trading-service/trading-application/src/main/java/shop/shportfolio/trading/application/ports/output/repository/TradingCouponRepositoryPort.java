package shop.shportfolio.trading.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.CouponInfo;

import java.util.Optional;
import java.util.UUID;

public interface TradingCouponRepositoryPort {

    CouponInfo saveCouponInfo(CouponInfo couponInfo);
    Optional<CouponInfo> findCouponInfoByUserId(UUID userId);
}
