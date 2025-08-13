package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class AnalysisTime extends ValueObject<LocalDateTime> {
    public AnalysisTime(LocalDateTime value) {
        super(value);
    }
}
