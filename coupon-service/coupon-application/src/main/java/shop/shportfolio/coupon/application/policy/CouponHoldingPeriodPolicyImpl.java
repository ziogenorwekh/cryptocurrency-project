package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;

import java.time.LocalDate;

@Component
public class CouponHoldingPeriodPolicyImpl implements CouponHoldingPeriodPolicy {
    @Override
    public ExpiryDate calculateExpiryDate() {
        return new ExpiryDate(LocalDate.now().plusMonths(2));
    }
}
