package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class CancelledAt extends ValueObject<LocalDateTime> {
    public CancelledAt(LocalDateTime value) {
        super(value);
    }
}
