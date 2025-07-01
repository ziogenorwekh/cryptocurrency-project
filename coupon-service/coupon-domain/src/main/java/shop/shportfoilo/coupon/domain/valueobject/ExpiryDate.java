package shop.shportfoilo.coupon.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDate;

@Getter
public class ExpiryDate extends ValueObject<LocalDate> {
    public ExpiryDate(LocalDate value) {
        super(value);
    }
}
