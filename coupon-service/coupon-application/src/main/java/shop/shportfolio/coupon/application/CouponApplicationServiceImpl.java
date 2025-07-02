package shop.shportfolio.coupon.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponListTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQuery;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.handler.CouponCreateHandler;
import shop.shportfolio.coupon.application.handler.CouponTrackHandler;
import shop.shportfolio.coupon.application.handler.CouponUpdateHandler;
import shop.shportfolio.coupon.application.mapper.CouponDataMapper;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentPort;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class CouponApplicationServiceImpl implements CouponApplicationService {

    private final CouponCreateHandler couponCreateHandler;
    private final CouponDataMapper couponDataMapper;
    private final CouponTrackHandler couponTrackHandler;
    private final CouponUpdateHandler couponUpdateHandler;
    private final PaymentPort paymentPort;

    @Autowired
    public CouponApplicationServiceImpl(CouponCreateHandler couponCreateHandler,
                                        CouponDataMapper couponDataMapper,
                                        CouponTrackHandler couponTrackHandler,
                                        CouponUpdateHandler couponUpdateHandler,
                                        PaymentPort paymentPort) {
        this.couponCreateHandler = couponCreateHandler;
        this.couponDataMapper = couponDataMapper;
        this.couponTrackHandler = couponTrackHandler;
        this.couponUpdateHandler = couponUpdateHandler;
        this.paymentPort = paymentPort;
    }

    @Override
    public CouponCreatedResponse createCoupon(CouponCreateCommand command) {
        Coupon coupon = couponCreateHandler.createCoupon(command);
        return couponDataMapper.couponToCouponCreatedResponse(coupon);
    }

    @Override
    public List<CouponTrackQueryResponse> trackCouponList(CouponListTrackQuery command) {
        List<Coupon> list = couponTrackHandler.findCouponsByUserId(command);
        return list.stream().map(couponDataMapper::couponToCouponListTrackQueryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponTrackQueryResponse trackCoupon(CouponTrackQuery command) {
        Coupon coupon = couponTrackHandler.findCouponById(command);
        return couponDataMapper.couponToCouponListTrackQueryResponse(coupon);
    }

    @Override
    public CouponUseUpdateResponse useCoupon(CouponUseUpdateCommand command) {
        // 결제 먼저 해보고 성공하면 도메인 변경으로

        Coupon coupon = couponUpdateHandler.useCoupon(command);
        return couponDataMapper.couponToCouponUpdateResponse(coupon);
    }

    @Override
    public CouponReactiveUpdateResponse reactiveCoupon(CouponReactiveUpdateCommand command) {
        // 결제 먼저 해보고 성공하면 도메인 변경으로
        return null;
    }
}
