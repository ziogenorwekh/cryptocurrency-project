package shop.shportfoilo.coupon.domain.entity;

import lombok.Getter;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Payment extends BaseEntity<PaymentId> {

    private final UserId userId;
    private final CouponId couponId;
    private final PaymentKey paymentKey;
    private final OrderPrice totalAmount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private final CreatedAt requestedAt;
    private final PaidAt paidAt;
    private final Description description;
    private final String rawResponse;
    private CancelReason cancelReason;
    private CancelledAt cancelledAt;

    private Payment(PaymentId paymentId, UserId userId, CouponId couponId, PaymentKey paymentKey, OrderPrice totalAmount,
                    PaymentMethod paymentMethod, PaymentStatus status,
                    Description description, String rawResponse) {
        this.couponId = couponId;
        setId(paymentId);
        this.userId = userId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.requestedAt = new CreatedAt(LocalDateTime.now());
        this.paidAt = new  PaidAt(LocalDateTime.now());
        this.description = description;
        this.rawResponse = rawResponse;
        this.cancelReason = null;
        this.cancelledAt = null;
    }

    public static Payment createPayment(UserId userId, CouponId couponId, PaymentKey paymentKey, OrderPrice totalAmount,
                                        PaymentMethod paymentMethod, PaymentStatus status,
                                        Description description, String rawResponse) {
        PaymentId paymentId = new PaymentId(UUID.randomUUID());
        return new Payment(paymentId, userId, couponId, paymentKey,
                totalAmount, paymentMethod,
                status, description, rawResponse);
    }


    public void cancel(String reason) {
        if (status != PaymentStatus.DONE) {
            throw new CouponDomainException("Payment cannot be canceled unless DONE.");
        }
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = new CancelReason(reason);
        this.cancelledAt = new CancelledAt(LocalDateTime.now());
    }

    public boolean isReady() {
        return this.status == PaymentStatus.READY;
    }

    public boolean isInProgress() {
        return this.status == PaymentStatus.IN_PROGRESS;
    }

    public boolean isWaitingForDeposit() {
        return this.status == PaymentStatus.WAITING_FOR_DEPOSIT;
    }

    public boolean isDone() {
        return this.status == PaymentStatus.DONE;
    }

    public boolean isCanceled() {
        return this.status == PaymentStatus.CANCELED;
    }

    public boolean isPartialCanceled() {
        return this.status == PaymentStatus.PARTIAL_CANCELED;
    }

    public boolean isAborted() {
        return this.status == PaymentStatus.ABORTED;
    }

    public boolean isExpired() {
        return this.status == PaymentStatus.EXPIRED;
    }

    public boolean isFailed() {
        return this.status == PaymentStatus.ABORTED || this.status == PaymentStatus.EXPIRED;
    }
}
