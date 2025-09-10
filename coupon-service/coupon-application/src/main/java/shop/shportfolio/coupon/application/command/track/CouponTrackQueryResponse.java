package shop.shportfolio.coupon.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfoilo.coupon.domain.valueobject.CouponStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CouponTrackQueryResponse {

    private final UUID couponId;
    private final UUID owner;
    private final Integer feeDiscount;
    private final LocalDate validUntil;
    private final LocalDate issuedAt;
    private final String couponCode;
    private final CouponStatus status;
}
