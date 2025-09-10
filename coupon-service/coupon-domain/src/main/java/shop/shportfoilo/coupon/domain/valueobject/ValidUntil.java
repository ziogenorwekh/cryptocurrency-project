package shop.shportfoilo.coupon.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDate;

@Getter
public class ValidUntil extends ValueObject<LocalDate> {
    public ValidUntil(LocalDate value) {
        super(value);
    }
}
