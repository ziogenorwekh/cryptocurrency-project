package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;

import java.time.LocalDate;

@Component
public class CouponUsageDatePolicyImpl implements CouponUsageDatePolicy {

    @Override
    public UsageExpiryDate calculateExpiryDate() {
        return new UsageExpiryDate(LocalDate.now().plusMonths(1));
    }
}
