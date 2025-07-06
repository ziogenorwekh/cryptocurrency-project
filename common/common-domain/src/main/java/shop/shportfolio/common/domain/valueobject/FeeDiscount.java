package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class FeeDiscount extends ValueObject<Integer> implements Comparable<FeeDiscount> {

    public FeeDiscount(Integer value) {
        super(value);
    }


    public Boolean isZero() {
        return value==0;
    }

    public Boolean isPositive() {
        return value>0;
    }
    public Boolean isNegative() {
        return value<0;
    }

    public static FeeDiscount ofSilver() {
        return new FeeDiscount(10);
    }

    public static FeeDiscount ofGold() {
        return new FeeDiscount(20);
    }
    public static FeeDiscount ofUser() {
        return new FeeDiscount(30);
    }
    public static FeeDiscount ofVip() {
        return new FeeDiscount(40);
    }

    // 실제 계산용 할인율 (0.0 ~ 1.0) 반환
    public BigDecimal getRatio() {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100));
    }

    @Override
    public int compareTo(FeeDiscount other) {
        return Integer.compare(this.value, other.value);
    }
}
