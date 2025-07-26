package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class CouponUsageDatePolicyImpl implements CouponUsageDatePolicy {

    @Override
    public UsageExpiryDate calculateExpiryDate() {
        return new UsageExpiryDate(LocalDate.now(ZoneOffset.UTC).plusMonths(1));
    }
}
