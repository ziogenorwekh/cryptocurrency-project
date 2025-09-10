package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.valueobject.ValidUntil;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class CouponHoldingPeriodPolicyImpl implements CouponHoldingPeriodPolicy {
    @Override
    public ValidUntil calculateExpiryDate() {
        return new ValidUntil(LocalDate.now(ZoneOffset.UTC).plusMonths(2));
    }
}
