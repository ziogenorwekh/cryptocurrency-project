package shop.shportfolio.coupon.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CouponUsageTrackQueryResponse {

    private final UUID couponId;
    private final UUID couponUsageId;
    private final UUID userId;
    private final LocalDate expiryDate;
    private final LocalDate issuedDate;
}
