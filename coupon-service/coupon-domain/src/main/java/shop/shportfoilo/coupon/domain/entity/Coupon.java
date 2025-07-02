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
    private final FeeDiscount feeDiscount;
    private final ExpiryDate expiryDate;
    private final IssuedAt issuedAt;
    private final CouponCode couponCode;
    private CouponStatus status;

    public Coupon(CouponId couponId, OwnerId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate,
                  IssuedAt issuedAt, CouponCode couponCode, CouponStatus status) {
        setId(couponId);
        this.owner = owner;
        this.feeDiscount = feeDiscount;
        this.issuedAt = issuedAt;
        this.expiryDate = expiryDate;
        this.couponCode = couponCode;
        this.status = status;
    }

    public static Coupon createCoupon(OwnerId owner,
                                      FeeDiscount feeDiscount,
                                      ExpiryDate expiryDate,
                                      CouponCode couponCode) {
        CouponId couponId = new CouponId(UUID.randomUUID());
        IssuedAt issuedAt = new IssuedAt(LocalDate.now());
        Coupon coupon = new Coupon(couponId, owner, feeDiscount, expiryDate, issuedAt, couponCode, CouponStatus.ACTIVE);
        coupon.validateDiscountRate();
        coupon.status = CouponStatus.ACTIVE;
        return coupon;
    }

    public void useCoupon() {
        validateForUse();
        this.status = CouponStatus.USED;

    }

    public Boolean isExpired() {
        return this.expiryDate.getValue().isBefore(LocalDate.now());
    }

    public void updateStatusIfCouponExpired() {
        if (isExpired() && !this.status.equals(CouponStatus.EXPIRED)) {
            this.status = CouponStatus.EXPIRED;
        }
    }

    public void cancel() {
        if (this.status.isFinal()) {
            throw new CouponDomainException("Cannot cancel a coupon that is already used or expired.");
        }
        this.status = CouponStatus.CANCELED;
    }

    public void reactivate() {
        if (!this.status.equals(CouponStatus.CANCELED)) {
            throw new CouponDomainException("Only canceled coupons can be reactivated.");
        }
        if (isExpired()) {
            throw new CouponDomainException("Cannot reactivate an expired coupon.");
        }
        this.status = CouponStatus.ACTIVE;
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
        if (feeDiscount.isNegative()) {
            throw new CouponDomainException("Discount cannot be negative.");
        }
        if (feeDiscount.isZero()) {
            throw new CouponDomainException("Discount cannot be zero.");
        }
    }
}
