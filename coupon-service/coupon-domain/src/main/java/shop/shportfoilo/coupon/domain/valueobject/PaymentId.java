package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class PaymentId extends ValueObject<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
