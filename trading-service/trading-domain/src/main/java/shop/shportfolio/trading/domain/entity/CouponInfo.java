package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;

@Getter
public class CouponInfo extends BaseEntity<CouponId> {
    private final UserId userId;
    private final FeeDiscount feeDiscount;
    private final IssuedAt issuedAt;
    private final UsageExpiryDate usageExpiryDate;

    @Builder
    public CouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                      IssuedAt issuedAt, UsageExpiryDate usageExpiryDate) {
        setId(couponId);
        this.userId = userId;
        this.feeDiscount = feeDiscount;
        this.issuedAt = issuedAt;
        this.usageExpiryDate = usageExpiryDate;
    }

    public static CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                                              IssuedAt issuedAt, UsageExpiryDate usageExpiryDate) {
        return new CouponInfo(couponId, userId, feeDiscount, issuedAt, usageExpiryDate);
    }
}
