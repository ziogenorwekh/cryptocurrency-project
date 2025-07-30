package shop.shportfolio.coupon.application.schedular;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.event.CouponExpiredEvent;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponExpiredPublisher;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
public class CouponExpirationDateSchedular {

    private final CouponDomainService couponDomainService;
    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponExpiredPublisher couponExpiredPublisher;
    @Autowired
    public CouponExpirationDateSchedular(CouponDomainService couponDomainService,
                                         CouponRepositoryPort couponRepositoryPort,
                                         CouponExpiredPublisher couponExpiredPublisher) {
        this.couponDomainService = couponDomainService;
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponExpiredPublisher = couponExpiredPublisher;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredCoupons() {
        log.info("Start coupon expiration check at {}", System.currentTimeMillis());
        List<Coupon> usages = couponRepositoryPort
                .findCouponByExpiredDate(LocalDate.now(ZoneOffset.UTC));
        usages.forEach(coupon -> {
            CouponExpiredEvent couponExpiredEvent = couponDomainService.updateStatusIfCouponExpired(coupon);
            // 카프카 퍼블리시
            couponExpiredPublisher.publish(couponExpiredEvent);
            // 쿠폰Usage 삭제
            couponRepositoryPort.removeCouponUsageByCouponIdAndUserId(coupon.getId().getValue(),
                    coupon.getOwner().getValue());
        });
        log.info("End coupon expiration check at {}", System.currentTimeMillis());
    }
}
