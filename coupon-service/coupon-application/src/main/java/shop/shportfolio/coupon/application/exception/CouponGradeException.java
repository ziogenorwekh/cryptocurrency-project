package shop.shportfolio.coupon.application.exception;

import shop.shportfoilo.coupon.domain.exception.CouponDomainException;

public class CouponGradeException extends CouponApplicationException {
    public CouponGradeException(String message) {
        super(message);
    }
}
