package shop.shportfolio.coupon.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfolio.common.domain.valueobject.RoleType;

import java.time.LocalDate;
import java.util.List;

@Component
public class AfterIssueExpireAtPolicy implements ExpireAtPolicy {


    @Override
    public ExpiryDate calculate(List<RoleType> roles) {
        LocalDate aMonth = LocalDate.now().plusMonths(1);
        return new  ExpiryDate(aMonth);
    }
}
