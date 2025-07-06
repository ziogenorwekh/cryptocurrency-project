package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class CouponUsageId extends ValueObject<UUID> {

    public CouponUsageId(UUID value) {
        super(value);
    }
}
