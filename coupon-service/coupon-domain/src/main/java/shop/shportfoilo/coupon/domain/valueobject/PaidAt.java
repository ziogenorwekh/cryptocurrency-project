package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class PaidAt extends ValueObject<LocalDateTime> {
    public PaidAt(LocalDateTime value) {
        super(value);
    }
}
