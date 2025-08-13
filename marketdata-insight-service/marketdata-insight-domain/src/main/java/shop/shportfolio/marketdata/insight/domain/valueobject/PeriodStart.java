package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class PeriodStart extends ValueObject<LocalDateTime> {
    public PeriodStart(LocalDateTime value) {
        super(value);
    }
}
