package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.coupon.application.exception.CouponGradeException;

import java.time.LocalDate;
import java.util.List;

@Component
public class RoleBasedExpireAtPolicy implements ExpireAtPolicy {

    @Override
    public ExpiryDate calculate(List<RoleType> roles) {
        LocalDate baseDate = LocalDate.now();

        LocalDate maxExpiry = roles.stream()
                .map(role -> switch (role) {
                    case USER -> baseDate.plusMonths(1);
                    case SILVER -> baseDate.plusMonths(1).plusDays(15);
                    case GOLD -> baseDate.plusMonths(2);
                    case VIP -> baseDate.plusMonths(3);
                    default -> throw new CouponGradeException("RoleType not supported");
                })
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new CouponGradeException("No roles provided"));
        return new ExpiryDate(maxExpiry);
    }
}
