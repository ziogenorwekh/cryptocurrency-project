package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQuery;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

import java.util.List;

@Component
public class CouponTrackHandler {

    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponDomainService couponDomainService;

    @Autowired
    public CouponTrackHandler(CouponRepositoryPort couponRepositoryPort, CouponDomainService couponDomainService) {
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponDomainService = couponDomainService;
    }

    public List<Coupon> findCouponsByUserId(CouponListTrackQuery couponListTrackQuery) {
        return couponRepositoryPort.findByUserId(couponListTrackQuery.getUserId());
    }

    public Coupon findCouponById(CouponTrackQuery couponTrackQuery) {
        return couponRepositoryPort.findByUserIdAndCouponId(couponTrackQuery.getUserId(), couponTrackQuery.getCouponId())
                .orElseThrow(()-> new CouponNotFoundException(String.format("coupon id %s not found",
                        couponTrackQuery.getCouponId())));
    }
}
