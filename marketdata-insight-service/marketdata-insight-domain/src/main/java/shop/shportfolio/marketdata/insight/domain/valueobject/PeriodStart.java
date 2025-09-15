package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class PeriodStart extends ValueObject<OffsetDateTime> {
    public PeriodStart(OffsetDateTime value) {
        super(value);
    }
}
