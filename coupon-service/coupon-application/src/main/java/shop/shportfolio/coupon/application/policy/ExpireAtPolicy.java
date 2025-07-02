package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfolio.common.domain.valueobject.RoleType;

import java.time.LocalDate;
import java.util.List;

public interface ExpireAtPolicy {
    ExpiryDate calculate(List<RoleType> roles);
}
