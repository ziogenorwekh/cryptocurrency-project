package shop.shportfoilo.coupon.domain.entity;

import lombok.Getter;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.util.UUID;

@Getter
public class Payment extends BaseEntity<PaymentId> {

    private final UserId userId;
    private final PaymentKey paymentKey;
    private final OrderPrice totalAmount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final CreatedAt requestedAt;
    private final PaidAt paidAt;
    private final Description description;
    private final String rawResponse;

    private Payment(PaymentId paymentId, UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                   PaymentMethod paymentMethod, PaymentStatus status,
                   CreatedAt requestedAt, PaidAt paidAt,
                   Description description, String rawResponse) {
        setId(paymentId);
        this.userId = userId;
        this.paymentKey = paymentKey;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.requestedAt = requestedAt;
        this.paidAt = paidAt;
        this.description = description;
        this.rawResponse = rawResponse;
    }

    public static Payment createPayment(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                                        PaymentMethod paymentMethod, PaymentStatus status,
                                        CreatedAt requestedAt, PaidAt paidAt,
                                        Description description, String rawResponse) {
        PaymentId paymentId = new PaymentId(UUID.randomUUID());
        return new Payment(paymentId, userId, paymentKey, totalAmount,
                paymentMethod, status,
                requestedAt, paidAt,
                description, rawResponse);
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
