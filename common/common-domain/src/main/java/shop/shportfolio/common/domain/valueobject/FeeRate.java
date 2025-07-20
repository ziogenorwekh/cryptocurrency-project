package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeRate extends ValueObject<BigDecimal> {
    public FeeRate(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("FeeRate must be between 0 and 1");
        }
    }

    /**
     * 할인율을 적용한 새로운 FeeRate 반환
     *
     * @param discountRatio 0~1 사이의 비율
     * @return 할인 적용된 FeeRate
     */
    public FeeRate applyDiscount(BigDecimal discountRatio) {
        if (discountRatio.compareTo(BigDecimal.ZERO) < 0 || discountRatio.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount ratio must be between 0 and 1");
        }

        BigDecimal discounted = getValue().multiply(BigDecimal.ONE.subtract(discountRatio))
                .setScale(8, RoundingMode.HALF_UP);
        return new FeeRate(discounted);
    }

    // 추가: 현재 FeeRate 값 반환
    public BigDecimal getRate() {
        return getValue();
    }
    /**
     * 거래 금액 * 수량 * 수수료율로 수수료 금액을 계산
     *
     * @param price    1개당 가격
     * @param quantity 수량
     * @return 수수료 금액
     */
    public FeeAmount calculateFeeAmount(OrderPrice price, Quantity quantity) {
        BigDecimal tradeAmount = price.getValue().multiply(quantity.getValue());
        BigDecimal fee = tradeAmount.multiply(getValue()).setScale(8, RoundingMode.HALF_UP);
        return new FeeAmount(fee);
    }

    public FeeAmount calculateFeeTotalAmount(OrderPrice price) {
        BigDecimal fee = price.getValue().multiply(getValue()).setScale(8, RoundingMode.HALF_UP);
        return new FeeAmount(fee);
    }
}
