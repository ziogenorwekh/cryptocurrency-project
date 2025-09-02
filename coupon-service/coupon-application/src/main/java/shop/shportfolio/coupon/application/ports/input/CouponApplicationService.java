package shop.shportfolio.coupon.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.*;

import java.util.List;

public interface CouponApplicationService {

    /***
     * 쿠폰 생성
     * @param command
     * @return
     */
    CouponCreatedResponse createCoupon(@Valid CouponCreateCommand command);

    /***
     * 쿠폰 리스트 조회
     * @param command
     * @return
     */
    List<CouponTrackQueryResponse> trackCouponList(@Valid CouponListTrackQuery command);

    /***
     * 쿠폰 단건 조회
     * @param command
     * @return
     */
    CouponTrackQueryResponse trackCoupon(@Valid CouponTrackQuery command);

    /***
     * 쿠폰 사용 처리
     * @param command
     * @return
     */
    CouponUseUpdateResponse useCoupon(@Valid CouponUseUpdateCommand command);

    /***
     * 쿠폰 사용 취소 처리
     * @param command
     * @return
     */
    CouponReactiveUpdateResponse reactiveCoupon(@Valid CouponReactiveUpdateCommand command);

    /***
     * 결제 관련 쿠폰 조회
     * @param command
     * @return
     */
    PaymentTrackQueryResponse trackPayment(@Valid PaymentTrackQuery command);

    /***
     * 쿠폰 취소 처리
     * @param command
     * @return
     */
    CouponCancelUpdateResponse cancelCoupon(@Valid CouponCancelUpdateCommand command);

    /***
     * 쿠폰 사용 내역 조회
     * @param command
     * @return
     */
    CouponUsageTrackQueryResponse trackCouponUsage(@Valid CouponUsageTrackQuery command);


}
