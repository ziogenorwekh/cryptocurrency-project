package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeAmount extends ValueObject<BigDecimal> {
    public FeeAmount(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("FeeAmount cannot be negative");
        }
    }

    public static FeeAmount zero() {
        return new FeeAmount(BigDecimal.ZERO);
    }

    /**
     * 가격과 수수료율로 수수료 금액 계산
     *
     * @param price   1개당 가격
     * @param feeRate 수수료율 (0~1 사이 BigDecimal)
     * @return 수수료 금액
     */
    public static FeeAmount calculateFeeAmount(OrderPrice price, BigDecimal feeRate) {
        if (feeRate.compareTo(BigDecimal.ZERO) < 0 || feeRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("FeeRate must be between 0 and 1");
        }
        BigDecimal fee = price.getValue().multiply(feeRate).setScale(8, RoundingMode.HALF_UP);
        return new FeeAmount(fee);
    }
}
