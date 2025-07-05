package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class ProfitLossId extends ValueObject<UUID> {
    public ProfitLossId(UUID value) {
        super(value);
    }
}
