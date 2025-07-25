package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class UsageExpiryDate extends ValueObject<LocalDate> {
    public UsageExpiryDate(LocalDate value) {
        super(value);
    }

    public boolean isExpired() {
        return getValue().isBefore(LocalDate.now(ZoneOffset.UTC));
    }

    public static UsageExpiryDate from(LocalDate value) {
        return new UsageExpiryDate(value);
    }
}
