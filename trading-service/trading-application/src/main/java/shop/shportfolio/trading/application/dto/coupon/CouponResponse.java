package shop.shportfolio.trading.application.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.*;

@Getter
@Builder
@AllArgsConstructor
public class CouponResponse {
    private final CouponId couponId;
    private final UserId owner;
    private final FeeDiscount feeDiscount;
    private final IssuedAt issuedAt;
    private final UsageExpiryDate expiryDate;

}
