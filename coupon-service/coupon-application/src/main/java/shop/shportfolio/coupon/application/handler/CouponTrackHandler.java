package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponUsageTrackQuery;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.exception.CouponUsageNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

import java.util.List;

@Component
public class CouponTrackHandler {

    private final CouponRepositoryPort couponRepositoryPort;

    @Autowired
    public CouponTrackHandler(CouponRepositoryPort couponRepositoryPort) {
        this.couponRepositoryPort = couponRepositoryPort;
    }

    public List<Coupon> findCouponsByUserId(CouponListTrackQuery couponListTrackQuery) {
        return couponRepositoryPort.findByUserId(couponListTrackQuery.getUserId());
    }

    public Coupon findCouponById(CouponTrackQuery couponTrackQuery) {
        return couponRepositoryPort.findByUserIdAndCouponId(couponTrackQuery.getUserId(), couponTrackQuery.getCouponId())
                .orElseThrow(()-> new CouponNotFoundException(String.format("coupon id %s not found",
                        couponTrackQuery.getCouponId())));
    }

    public CouponUsage findCouponUsageByUserIdAndCouponId(CouponUsageTrackQuery couponUsageTrackQuery) {
        return couponRepositoryPort.findCouponUsageByUserIdAndCouponId(couponUsageTrackQuery.getUserId(),
                couponUsageTrackQuery.getCouponId()).orElseThrow(()->new CouponUsageNotFoundException(
                        String.format("coupon id %s not found", couponUsageTrackQuery.getCouponId())
        ));
    }
}
