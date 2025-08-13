package shop.shportfolio.marketdata.insight.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class MomentumScore extends ValueObject<BigDecimal> {
    public MomentumScore(BigDecimal value) {
        super(value);
    }

    public static MomentumScore of(BigDecimal value) {
        return new MomentumScore(value);
    }

    public MomentumScore add(MomentumScore momentumScore) {
        return new MomentumScore(this.value.add(momentumScore.value));
    }

    public MomentumScore subtract(MomentumScore momentumScore) {
        return new MomentumScore(this.value.subtract(momentumScore.value));
    }

    public MomentumScore multiply(BigDecimal factor) {
        return new MomentumScore(this.value.multiply(factor));
    }

    public MomentumScore divide(BigDecimal divisor) {
        return new MomentumScore(this.value.divide(divisor));
    }
}
