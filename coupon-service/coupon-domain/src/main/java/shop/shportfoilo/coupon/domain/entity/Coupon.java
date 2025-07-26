package shop.shportfoilo.coupon.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Getter
public class Coupon extends AggregateRoot<CouponId> {

    private final UserId owner;
    private final FeeDiscount feeDiscount;
    private final ExpiryDate expiryDate;
    private final IssuedAt issuedAt;
    private final CouponCode couponCode;
    private CouponStatus status;

    @Builder
    public Coupon(CouponId couponId, UserId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate,
                  IssuedAt issuedAt, CouponCode couponCode, CouponStatus status) {
        setId(couponId);
        this.owner = owner;
        this.feeDiscount = feeDiscount;
        this.issuedAt = issuedAt;
        this.expiryDate = expiryDate;
        this.couponCode = couponCode;
        this.status = status;
    }

    public static Coupon createCoupon(UserId owner,
                                      FeeDiscount feeDiscount,
                                      ExpiryDate expiryDate,
                                      CouponCode couponCode) {
        CouponId couponId = new CouponId(UUID.randomUUID());
        IssuedAt issuedAt = IssuedAt.now();
        Coupon coupon = new Coupon(couponId, owner, feeDiscount, expiryDate, issuedAt,
                couponCode, CouponStatus.ACTIVE);
        coupon.validateDiscountRate();
        coupon.status = CouponStatus.ACTIVE;
        return coupon;
    }

    public void useCoupon(String code) {
        isAlreadyUse();
        validateForUse();
        if (!this.couponCode.getValue().equals(code)) {
            throw new CouponDomainException("Coupon code is invalid");
        }
        this.status = CouponStatus.USED;
    }

    public Boolean isExpired() {
        return this.expiryDate.getValue().isBefore(LocalDate.now(ZoneOffset.UTC));
    }

    public CouponUsage createCouponUsage(UsageExpiryDate expiryDate) {
        if (this.status != CouponStatus.USED) {
            throw new CouponDomainException("Coupon status must be USED to create CouponUsage.");
        }
        return CouponUsage.createCouponUsage(this.getId(), this.getOwner(),
                new IssuedAt(LocalDate.now(ZoneOffset.UTC)), expiryDate);
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

    private void isAlreadyUse() {
        if (status == CouponStatus.USED) {
            throw new CouponDomainException("Coupon is already used.");
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
