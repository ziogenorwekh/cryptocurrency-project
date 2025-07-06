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
import shop.shportfolio.coupon.application.handler.PaymentHandler;
import shop.shportfolio.coupon.application.mapper.CouponDataMapper;
import shop.shportfolio.coupon.application.policy.*;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponUsedPublisher;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentTossAPIPort;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;
import shop.shportfolio.coupon.application.ports.output.repository.PaymentRepositoryPort;

@Configuration
public class CouponMockBean {


    @Bean
    public CouponDataMapper couponDataMapper() {
        return new CouponDataMapper();
    }

    @Bean
    public CouponRepositoryPort couponRepositoryAdapter() {
        return Mockito.mock(CouponRepositoryPort.class);
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
    public CouponHoldingPeriodPolicy couponHoldingPeriodPolicy() {
        return new CouponHoldingPeriodPolicyImpl();
    }

    @Bean
    public CouponCreateHandler couponCreateHandler() {
        return new CouponCreateHandler(couponDomainService(), couponRepositoryAdapter()
                , couponDiscountPolicy(),couponHoldingPeriodPolicy());
    }

    @Bean
    public CouponTrackHandler couponTrackHandler() {
        return new CouponTrackHandler(couponRepositoryAdapter());
    }

    @Bean
    public CouponUsageDatePolicy couponUsageDatePolicy() {
        return new CouponUsageDatePolicyImpl();
    }

    @Bean
    public CouponUpdateHandler couponUpdateHandler() {
        return new CouponUpdateHandler(couponRepositoryAdapter(),couponDomainService(),
                couponUsedPublisher(),couponUsageDatePolicy());
    }

    @Bean
    public CouponUsedPublisher couponUsedPublisher() {
        return Mockito.mock(CouponUsedPublisher.class);
    }

    @Bean
    public PaymentTossAPIPort paymentPort() {
        return Mockito.mock(PaymentTossAPIPort.class);
    }

    @Bean
    public PaymentRepositoryPort paymentRepositoryAdapter() {
        return Mockito.mock(PaymentRepositoryPort.class);
    }

    @Bean
    public PaymentHandler paymentHandler() {
        return new PaymentHandler(paymentRepositoryAdapter(), couponDomainService());
    }

    @Bean
    public CouponApplicationService couponApplicationService() {
        return new CouponApplicationServiceImpl(couponCreateHandler(),
                couponDataMapper(),
                couponTrackHandler(),
                couponUpdateHandler(),paymentPort(),paymentHandler());
    }
}
