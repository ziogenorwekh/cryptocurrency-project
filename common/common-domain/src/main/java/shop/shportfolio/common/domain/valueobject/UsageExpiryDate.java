package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDate;

public class UsageExpiryDate extends ValueObject<LocalDate> {
    public UsageExpiryDate(LocalDate value) {
        super(value);
    }
}
