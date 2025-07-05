package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class RealizedProfitLoss extends ValueObject<BigDecimal> {
    public RealizedProfitLoss(BigDecimal value) {
        super(value);
    }
}
