package shop.shportfolio.coupon.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CouponUsedResponse {
    private final UUID couponId;
    private final UUID userId;
    private final LocalDate issuedAt;
    private final LocalDate expiryDate;
}
