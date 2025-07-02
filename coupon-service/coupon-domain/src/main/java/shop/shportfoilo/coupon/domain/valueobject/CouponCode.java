package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class CouponCode extends ValueObject<String> {
    private CouponCode(String value) {
        super(value);
    }

    public static CouponCode generate() {
        String code = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return new CouponCode(code);
    }
}
