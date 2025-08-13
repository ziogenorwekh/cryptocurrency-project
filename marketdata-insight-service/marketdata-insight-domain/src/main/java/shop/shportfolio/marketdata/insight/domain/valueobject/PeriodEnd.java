package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class PeriodEnd extends ValueObject<LocalDateTime> {
    public PeriodEnd(LocalDateTime value) {
        super(value);
    }
}
