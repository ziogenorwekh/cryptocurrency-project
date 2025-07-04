package shop.shportfolio.coupon.application.exception;

public class CouponApplicationException extends RuntimeException {
    public CouponApplicationException(String message) {
        super(message);
    }

    public CouponApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
