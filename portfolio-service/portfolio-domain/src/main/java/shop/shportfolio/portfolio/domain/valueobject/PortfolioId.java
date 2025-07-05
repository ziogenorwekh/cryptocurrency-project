package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class PortfolioId extends ValueObject<UUID> {

    public PortfolioId(UUID value) {
        super(value);
    }
}
