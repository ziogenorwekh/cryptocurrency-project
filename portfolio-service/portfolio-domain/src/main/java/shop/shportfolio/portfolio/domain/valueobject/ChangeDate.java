package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class ChangeDate extends ValueObject<LocalDateTime> {
    public ChangeDate(LocalDateTime value) {
        super(value);
    }
}
