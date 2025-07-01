package shop.shportfoilo.coupon.domain.valueobject;

public enum CouponStatus {
    ACTIVE,      // 발급되어 사용 가능
    USED,        // 사용 완료
    EXPIRED,     // 만료됨
    CANCELED     // 취소됨
}