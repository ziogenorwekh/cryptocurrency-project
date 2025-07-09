package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class OrderPrice extends ValueObject<BigDecimal> {

    public OrderPrice(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    public OrderPrice add(OrderPrice other) {
        return new OrderPrice(this.value.add(other.value));
    }

    public OrderPrice subtract(OrderPrice other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new OrderPrice(result);
    }

    public OrderPrice multiply(BigDecimal factor) {
        return new OrderPrice(this.value.multiply(factor));
    }

    public boolean isLessThanOrEqualTo(OrderPrice other) {
        return value.compareTo(other.value) <= 0;
    }

    public boolean isGreaterThanOrEqualTo(OrderPrice other) {
        return value.compareTo(other.value) >= 0;
    }

    public boolean isZeroOrLess() {
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }


    public boolean isOverTenPercent(BigDecimal targetPrice) {
        // 주문가가 기준가보다 10% 이상 높은지 판단
        BigDecimal diff = value.subtract(targetPrice);
        if (diff.compareTo(BigDecimal.ZERO) <= 0) {
            // 주문가가 기준가 이하일 땐 10% 초과 아님
            return false;
        }
        BigDecimal ratio = diff.divide(targetPrice, 8, BigDecimal.ROUND_HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }

    public boolean isUnderTenPercent(BigDecimal targetPrice) {
        // 주문가가 기준가보다 10% 이상 낮은지 판단
        BigDecimal diff = targetPrice.subtract(value);
        if (diff.compareTo(BigDecimal.ZERO) <= 0) {
            // 주문가가 기준가 이상일 땐 10% 이하 아님
            return false;
        }
        BigDecimal ratio = diff.divide(targetPrice, 8, BigDecimal.ROUND_HALF_UP);
        return ratio.compareTo(new BigDecimal("0.1")) > 0;
    }
}