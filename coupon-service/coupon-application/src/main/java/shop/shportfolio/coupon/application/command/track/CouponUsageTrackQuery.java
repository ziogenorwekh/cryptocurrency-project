package shop.shportfolio.coupon.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CouponUsageTrackQuery {
    private UUID userId;
    private UUID couponId;
}
