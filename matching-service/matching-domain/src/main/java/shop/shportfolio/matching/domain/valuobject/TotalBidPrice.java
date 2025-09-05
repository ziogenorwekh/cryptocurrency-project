package shop.shportfolio.matching.domain.valuobject;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

@Getter
public class TotalBidPrice extends ValueObject<BigDecimal> {
    public TotalBidPrice(BigDecimal value) {
        super(value);
    }
}
