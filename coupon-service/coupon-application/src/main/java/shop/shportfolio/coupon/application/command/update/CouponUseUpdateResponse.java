package shop.shportfolio.coupon.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.valueobject.CouponStatus;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CouponUseUpdateResponse {
    private final UUID couponId;
    private final UUID owner;
    private final Integer feeDiscount;
    private final String couponCode;
    private final CouponStatus status;
}
