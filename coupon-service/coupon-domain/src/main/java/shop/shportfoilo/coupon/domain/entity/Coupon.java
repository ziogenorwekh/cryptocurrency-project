package shop.shportfoilo.coupon.domain.entity;

import lombok.Getter;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.CouponId;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Coupon extends AggregateRoot<CouponId> {

    private final OwnerId owner;
    private final Discount discount;
    private final ExpiryDate expiryDate;
    private final IssuedAt issuedAt;
    private final CouponCode couponCode;
    private CouponStatus status;

    private Coupon(CouponId couponId, OwnerId owner, Discount discount, ExpiryDate expiryDate,
                   IssuedAt issuedAt, CouponCode couponCode) {
        setId(couponId);
        this.owner = owner;
        this.discount = discount;
        this.issuedAt = issuedAt;
        this.expiryDate = expiryDate;
        this.couponCode = couponCode;
    }

    public static Coupon createCoupon(OwnerId owner,
                                      Discount discount,
                                      ExpiryDate expiryDate,
                                      CouponCode couponCode) {
        CouponId couponId = new CouponId(UUID.randomUUID());
        IssuedAt issuedAt = new IssuedAt(LocalDate.now());
        Coupon coupon = new Coupon(couponId, owner, discount, expiryDate, issuedAt, couponCode);
        coupon.validateDiscountRate();
        coupon.status = CouponStatus.ACTIVE;
        return coupon;
    }

    public void useCoupon() {
        validateForUse();
        this.status = CouponStatus.USED;

    }

    public Boolean isExpired() {
        boolean isExpired = this.expiryDate.getValue().isBefore(LocalDate.now());
        if(isExpired) {
            this.status = CouponStatus.EXPIRED;
        }
        return isExpired;
    }

    private void validateForUse() {
        if (!this.status.equals(CouponStatus.ACTIVE)) {
            throw new CouponDomainException("Coupon is not active and cannot be used.");
        }
        if (isExpired()) {
            throw new CouponDomainException("Coupon is expired.");
        }
    }

    private void validateDiscountRate() {
        if(discount.isNegative()) {
            throw new CouponDomainException("Discount cannot be negative.");
        }
        if (discount.isZero()) {
            throw new CouponDomainException("Discount cannot be zero.");
        }
    }
}
