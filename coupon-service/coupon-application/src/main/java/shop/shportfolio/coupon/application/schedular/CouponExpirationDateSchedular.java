package shop.shportfolio.coupon.application.schedular;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    // @Transactional 제거!
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredCoupons() {
        log.info("Start coupon expiration check...");
        List<Coupon> expiredCoupons = couponRepositoryPort
                .findCouponByExpiredDate(LocalDate.now(ZoneOffset.UTC));
        expiredCoupons.forEach(coupon -> {
            processSingleExpiredCoupon(coupon);
        });
        log.info("End coupon expiration check.");
    }

    @Transactional // <--- 여기에 트랜잭션 추가!
    public void processSingleExpiredCoupon(Coupon coupon) {

        // 1. DB 상태 변경 (도메인 서비스 호출)
        CouponExpiredEvent couponExpiredEvent = couponDomainService
                .updateStatusIfCouponExpired(coupon);
        // 2. 쿠폰 Usage 삭제 (같은 트랜잭션에 포함)
        couponRepositoryPort.removeCouponUsageByCouponIdAndUserId(coupon.getId().getValue(),
                coupon.getOwner().getValue());
        try {
            couponExpiredPublisher.publish(couponExpiredEvent);
        } catch (Exception e) {
            log.error("Failed to publish expired coupon event for couponId: {}", coupon.getId().getValue(), e);
        }
    }
}
