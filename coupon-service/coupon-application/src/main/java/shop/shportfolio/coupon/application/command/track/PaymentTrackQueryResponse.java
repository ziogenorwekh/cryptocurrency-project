package shop.shportfolio.coupon.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentTrackQueryResponse {
    private final Long totalAmount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;
    private final String description;
    private final String cancelReason;
    private final LocalDateTime cancelledAt;
}
