package shop.shportfolio.portfolio.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Getter
public class Payment extends BaseEntity<PaymentId> {

    private final UserId userId;
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

    @Builder
    public Payment(PaymentId paymentId, UserId userId,
                   PaymentKey paymentKey, OrderPrice totalAmount,
                   PaymentMethod paymentMethod, PaymentStatus status,
                   Description description, String rawResponse) {
        setId(paymentId);
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.description = description;
        this.rawResponse = rawResponse;
        this.requestedAt = CreatedAt.now();
        this.paidAt = PaidAt.now();
        this.cancelReason = null;
        this.cancelledAt = null;
    }


    public static Payment create(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                                 PaymentMethod paymentMethod, PaymentStatus status,
                                 Description description, String rawResponse) {
        PaymentId paymentId = new PaymentId(UUID.randomUUID());
        return new Payment(paymentId, userId, paymentKey,
                totalAmount, paymentMethod,
                status, description, rawResponse);
    }


    public void cancel(String reason) {
        if (status != PaymentStatus.DONE) {
            throw new PortfolioDomainException("Payment cannot be canceled unless DONE.");
        }
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = new CancelReason(reason);
        this.cancelledAt = new CancelledAt(LocalDateTime.now(ZoneOffset.UTC));
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
