package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.RoleType;
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

    public static Discount ofSilver() {
        return new Discount(10);
    }

    public static Discount ofGold() {
        return new Discount(20);
    }
    public static Discount ofUser() {
        return new Discount(30);
    }
    public static Discount ofVip() {
        return new Discount(40);
    }
}
