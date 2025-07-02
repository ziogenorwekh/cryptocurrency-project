package shop.shportfolio.coupon.application.test.mockbean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.CouponDomainServiceImpl;
import shop.shportfolio.coupon.application.CouponApplicationServiceImpl;
import shop.shportfolio.coupon.application.handler.CouponCreateHandler;
import shop.shportfolio.coupon.application.handler.CouponTrackHandler;
import shop.shportfolio.coupon.application.handler.CouponUpdateHandler;
import shop.shportfolio.coupon.application.mapper.CouponDataMapper;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.policy.ExpireAtPolicy;
import shop.shportfolio.coupon.application.policy.RoleBasedExpireAtPolicy;
import shop.shportfolio.coupon.application.policy.RoleBasedExpireFeeDiscount;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentPort;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryAdapter;

@Configuration
public class CouponMockBean {


    @Bean
    public CouponDataMapper couponDataMapper() {
        return new CouponDataMapper();
    }

    @Bean
    public CouponRepositoryAdapter couponRepositoryAdapter() {
        return Mockito.mock(CouponRepositoryAdapter.class);
    }

    @Bean
    public ExpireAtPolicy expireAtPolicy() {
        return new RoleBasedExpireAtPolicy();
    }

    @Bean
    public CouponDiscountPolicy couponDiscountPolicy() {
        return new RoleBasedExpireFeeDiscount();
    }

    @Bean
    public CouponDomainService couponDomainService() {
        return new CouponDomainServiceImpl();
    }

    @Bean
    public CouponCreateHandler couponCreateHandler() {
        return new CouponCreateHandler(couponDomainService(), couponRepositoryAdapter()
                , couponDiscountPolicy(), expireAtPolicy());
    }

    @Bean
    public CouponTrackHandler couponTrackHandler() {
        return new CouponTrackHandler(couponRepositoryAdapter(), couponDomainService());
    }

    @Bean
    public CouponUpdateHandler couponUpdateHandler() {
        return new CouponUpdateHandler(couponRepositoryAdapter(),couponDomainService());
    }

    @Bean
    public PaymentPort paymentPort() {
        return Mockito.mock(PaymentPort.class);
    }

    @Bean
    public CouponApplicationService couponApplicationService() {
        return new CouponApplicationServiceImpl(couponCreateHandler(),
                couponDataMapper(),
                couponTrackHandler(),
                couponUpdateHandler(),paymentPort());
    }
}
