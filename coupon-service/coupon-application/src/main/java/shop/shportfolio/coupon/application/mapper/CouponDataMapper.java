package shop.shportfolio.coupon.application.mapper;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;

public class CouponDataMapper {


    public CouponCreatedResponse couponToCouponCreatedResponse(Coupon coupon) {
        return new CouponCreatedResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getExpiryDate().getValue(),
                coupon.getIssuedAt().getValue(), coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponTrackQueryResponse couponToCouponListTrackQueryResponse(Coupon coupon) {
        return new CouponTrackQueryResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getExpiryDate().getValue(), coupon.getIssuedAt().getValue(),
                coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponUseUpdateResponse couponToCouponUpdateResponse(Coupon coupon) {
        return new CouponUseUpdateResponse(coupon.getId().getValue(),coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(),coupon.getCouponCode().getValue(), coupon.getStatus());
    }
}
