package shop.shportfolio.trading.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OrderPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DefaultPriceLimitPolicy implements PriceLimitPolicy {
    @Override
    public boolean isOverTenPercentHigher(OrderPrice price, BigDecimal reference) {
        BigDecimal diff = price.getValue().subtract(reference);
        if (diff.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal ratio = diff.divide(reference, 8, RoundingMode.HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }

    @Override
    public boolean isOverTenPercentLower(OrderPrice price, BigDecimal reference) {
        BigDecimal diff = reference.subtract(price.getValue());
        if (diff.compareTo(BigDecimal.ZERO) <= 0) return false;
        BigDecimal ratio = diff.divide(reference, 8, RoundingMode.HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }
}
