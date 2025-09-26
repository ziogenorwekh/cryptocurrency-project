package shop.shportfolio.coupon.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.event.CouponUsedEvent;

@Getter
@AllArgsConstructor
public class CouponContext {

    private final CouponUsedEvent couponUsedEvent;
    private final CouponUsage couponUsage;
}
