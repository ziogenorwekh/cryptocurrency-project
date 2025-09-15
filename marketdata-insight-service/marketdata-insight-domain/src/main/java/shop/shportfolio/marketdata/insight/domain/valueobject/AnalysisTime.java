package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class AnalysisTime extends ValueObject<OffsetDateTime> {
    public AnalysisTime(OffsetDateTime value) {
        super(value);
    }
}
