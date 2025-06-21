package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class MarketItemTick extends ValueObject<BigDecimal> {

    public MarketItemTick(BigDecimal value) {
        super(value);
    }


}
