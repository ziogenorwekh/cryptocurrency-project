package shop.shportfolio.coupon.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.CouponDomainServiceImpl;

@Configuration
public class CouponApplicationConfiguration {

    @Bean
    public CouponDomainService couponDomainService() {
        return new CouponDomainServiceImpl();
    }
}
