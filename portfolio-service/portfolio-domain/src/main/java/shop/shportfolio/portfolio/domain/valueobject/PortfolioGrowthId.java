package shop.shportfolio.portfolio.domain.valueobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

@Getter
public class PortfolioGrowthId extends ValueObject<UUID> {
    public PortfolioGrowthId(UUID value) {
        super(value);
    }
}
