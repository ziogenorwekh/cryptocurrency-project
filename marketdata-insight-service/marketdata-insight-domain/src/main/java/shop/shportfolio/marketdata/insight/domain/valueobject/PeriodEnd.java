package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class PeriodEnd extends ValueObject<OffsetDateTime> {
    public PeriodEnd(OffsetDateTime value) {
        super(value);
    }
}
