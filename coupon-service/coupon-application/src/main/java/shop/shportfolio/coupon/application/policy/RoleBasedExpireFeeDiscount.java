package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.coupon.application.exception.CouponGradeException;

import java.time.LocalDate;
import java.util.List;

import static shop.shportfolio.common.domain.valueobject.RoleType.*;

@Component
public class RoleBasedExpireFeeDiscount implements CouponDiscountPolicy {
    @Override
    public FeeDiscount calculatorDiscount(List<RoleType> roles) {

        return roles.stream()
                .map(role -> switch (role) {
                    case USER -> FeeDiscount.ofUser();
                    case SILVER -> FeeDiscount.ofSilver();
                    case GOLD -> FeeDiscount.ofGold();
                    case VIP -> FeeDiscount.ofVip();
                    default -> throw new CouponGradeException("RoleType not supported");
                })
                .max(FeeDiscount::compareTo)
                .orElseThrow(() -> new CouponGradeException("No roles provided"));
    }
}
