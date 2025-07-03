package shop.shportfolio.coupon.application.exception;

public class PaymentNotFoundException extends CouponApplicationException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
