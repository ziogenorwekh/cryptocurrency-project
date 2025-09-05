package shop.shportfolio.matching.domain.valuobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

@Getter
public class TotalAskPrice extends ValueObject<BigDecimal> {
    public TotalAskPrice(BigDecimal value) {
        super(value);
    }
}
