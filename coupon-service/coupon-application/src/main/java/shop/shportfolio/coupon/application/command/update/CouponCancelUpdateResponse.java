package shop.shportfolio.coupon.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.valueobject.PaymentMethod;
import shop.shportfoilo.coupon.domain.valueobject.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CouponCancelUpdateResponse {
    private final UUID couponId;
    private final String paymentKey;
    private final String cancelReason;
    private final Long canceledAmount;
    private final String couponCode;
    private final String couponStatus;
    private final LocalDateTime canceledAt;
}
