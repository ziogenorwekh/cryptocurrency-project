package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

public class Discount extends ValueObject<Integer> {

    public Discount(Integer value) {
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
}
