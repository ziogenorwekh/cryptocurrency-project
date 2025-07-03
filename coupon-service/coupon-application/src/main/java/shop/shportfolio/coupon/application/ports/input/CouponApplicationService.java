package shop.shportfolio.coupon.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;

import java.util.List;

public interface CouponApplicationService {

    CouponCreatedResponse createCoupon(@Valid CouponCreateCommand command);

    List<CouponTrackQueryResponse> trackCouponList(@Valid CouponListTrackQuery command);

    CouponTrackQueryResponse trackCoupon(@Valid CouponTrackQuery command);

    CouponUseUpdateResponse useCoupon(@Valid CouponUseUpdateCommand command);

    CouponReactiveUpdateResponse  reactiveCoupon(@Valid CouponReactiveUpdateCommand command);

    PaymentTrackQueryResponse trackPayment(@Valid PaymentTrackQuery command);


}
