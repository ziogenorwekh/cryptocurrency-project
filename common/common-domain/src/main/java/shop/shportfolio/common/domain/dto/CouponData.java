package shop.shportfolio.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.IssuedAt;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.UserId;

@Getter
@AllArgsConstructor
@Builder
public class CouponData {
    private final CouponId couponId;
    private final UserId owner;
    private final FeeDiscount feeDiscount;
    private final IssuedAt issuedAt;
    private final UsageExpiryDate expiryDate;

}