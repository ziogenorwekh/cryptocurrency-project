package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon.CouponInfoEntity;

@Component
public class TradingCouponInfoDataAccessMapper {

    public CouponInfo couponInfoEntityToCouponInfo(CouponInfoEntity couponInfo) {
        return CouponInfo.builder()
                .couponId(new CouponId(couponInfo.getCouponId()))
                .userId(new UserId(couponInfo.getUserId()))
                .issuedAt(new IssuedAt(couponInfo.getIssuedAt()))
                .feeDiscount(new FeeDiscount(couponInfo.getFeeDiscount()))
                .usageExpiryDate(new UsageExpiryDate(couponInfo.getUsageExpiryDate()))
                .build();
    }

    public CouponInfoEntity couponInfoToCouponInfoEntity(CouponInfo couponInfo) {
        return CouponInfoEntity.builder()
                .couponId(couponInfo.getId().getValue())
                .userId(couponInfo.getUserId().getValue())
                .issuedAt(couponInfo.getIssuedAt().getValue())
                .feeDiscount(couponInfo.getFeeDiscount().getValue())
                .usageExpiryDate(couponInfo.getUsageExpiryDate().getValue())
                .build();
    }
}
