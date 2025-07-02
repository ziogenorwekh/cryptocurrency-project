package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQuery;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryAdapter;

import java.util.List;

@Component
public class CouponTrackHandler {

    private final CouponRepositoryAdapter couponRepositoryAdapter;
    private final CouponDomainService couponDomainService;

    @Autowired
    public CouponTrackHandler(CouponRepositoryAdapter couponRepositoryAdapter, CouponDomainService couponDomainService) {
        this.couponRepositoryAdapter = couponRepositoryAdapter;
        this.couponDomainService = couponDomainService;
    }

    public List<Coupon> findCouponsByUserId(CouponListTrackQuery couponListTrackQuery) {
        return couponRepositoryAdapter.findByUserId(couponListTrackQuery.getUserId());
    }

    public Coupon findCouponById(CouponTrackQuery couponTrackQuery) {
        return couponRepositoryAdapter.findByUserIdAndCouponId(couponTrackQuery.getUserId(), couponTrackQuery.getCouponId())
                .orElseThrow(()-> new CouponNotFoundException(String.format("coupon id %s not found",
                        couponTrackQuery.getCouponId())));
    }
}
