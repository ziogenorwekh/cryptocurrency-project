package shop.shportfoilo.coupon.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.valueobject.CouponUsageId;
import shop.shportfolio.common.domain.valueobject.IssuedAt;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.util.UUID;

@Getter
public class CouponUsage extends BaseEntity<CouponUsageId> {

    private final CouponId couponId;
    private final UserId userId;
    private final IssuedAt issuedAt;
    private final UsageExpiryDate expiryDate;

    @Builder
    private CouponUsage(CouponUsageId couponUsageId, CouponId couponId,
                       UserId userId, IssuedAt issuedAt, UsageExpiryDate expiryDate) {
        setId(couponUsageId);
        this.couponId = couponId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiryDate = expiryDate;
    }

    protected static CouponUsage createCouponUsage(CouponId couponId, UserId userId, IssuedAt issuedAt
            , UsageExpiryDate expiryDate) {
        CouponUsageId couponUsageId = new CouponUsageId(UUID.randomUUID());
        return new CouponUsage(couponUsageId, couponId, userId, issuedAt, expiryDate);
    }
}
