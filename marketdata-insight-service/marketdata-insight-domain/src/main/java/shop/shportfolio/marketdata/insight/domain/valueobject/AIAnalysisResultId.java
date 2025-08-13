package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class AIAnalysisResultId extends ValueObject<UUID> {

    public AIAnalysisResultId(UUID value) {
        super(value);
    }

    @Override
    public UUID getValue() {
        return super.getValue();
    }
}
