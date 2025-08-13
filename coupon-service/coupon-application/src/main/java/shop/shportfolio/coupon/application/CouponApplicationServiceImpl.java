package shop.shportfolio.coupon.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.*;
import shop.shportfolio.common.domain.dto.payment.PaymentRefundRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;
import shop.shportfolio.coupon.application.exception.PaymentException;
import shop.shportfolio.coupon.application.handler.CouponCreateHandler;
import shop.shportfolio.coupon.application.handler.CouponTrackHandler;
import shop.shportfolio.coupon.application.handler.CouponUpdateHandler;
import shop.shportfolio.coupon.application.handler.CouponPaymentHandler;
import shop.shportfolio.coupon.application.mapper.CouponDataMapper;
import shop.shportfolio.coupon.application.ports.input.CouponApplicationService;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentTossAPIPort;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class CouponApplicationServiceImpl implements CouponApplicationService {

    private final CouponCreateHandler couponCreateHandler;
    private final CouponDataMapper couponDataMapper;
    private final CouponTrackHandler couponTrackHandler;
    private final CouponUpdateHandler couponUpdateHandler;
    private final PaymentTossAPIPort paymentTossAPIPort;
    private final CouponPaymentHandler couponPaymentHandler;

    @Autowired
    public CouponApplicationServiceImpl(CouponCreateHandler couponCreateHandler,
                                        CouponDataMapper couponDataMapper,
                                        CouponTrackHandler couponTrackHandler,
                                        CouponUpdateHandler couponUpdateHandler,
                                        PaymentTossAPIPort paymentTossAPIPort,
                                        CouponPaymentHandler couponPaymentHandler) {
        this.couponCreateHandler = couponCreateHandler;
        this.couponDataMapper = couponDataMapper;
        this.couponTrackHandler = couponTrackHandler;
        this.couponUpdateHandler = couponUpdateHandler;
        this.paymentTossAPIPort = paymentTossAPIPort;
        this.couponPaymentHandler = couponPaymentHandler;
    }

    @Override
    public CouponCreatedResponse createCoupon(@Valid CouponCreateCommand command) {
        PaymentResponse paymentResponse = paymentTossAPIPort.pay(couponDataMapper.
                couponCreateCommandToPaymentRequest(command));
        log.info("paymentResponse -> {}", paymentResponse.toString());
        if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
            Coupon coupon = couponCreateHandler.createCoupon(command);
            couponPaymentHandler.save(coupon.getOwner(),paymentResponse, coupon.getId());
            return couponDataMapper.couponToCouponCreatedResponse(coupon);
        }
        throw new PaymentException("Payment failed");
    }

    /**
     * 특정 유저의 모든 쿠폰 조회
     *
     * @param command 특정 유저의 userId
     * @return 해당 유저가 발급한 모든 쿠폰 조회
     */
    @Override
    public List<CouponTrackQueryResponse> trackCouponList(@Valid CouponListTrackQuery command) {
        List<Coupon> list = couponTrackHandler.findCouponsByUserId(command);
        return list.stream().map(couponDataMapper::couponToCouponListTrackQueryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 단건 조회
     *
     * @param command 유저아이디와 쿠폰아이디를 통해서 특정 쿠폰에 대한 정보 객체
     * @return 조건에 부합하는 쿠폰에 대한 정보를 리턴
     */
    @Override
    public CouponTrackQueryResponse trackCoupon(@Valid CouponTrackQuery command) {
        Coupon coupon = couponTrackHandler.findCouponById(command);
        return couponDataMapper.couponToCouponListTrackQueryResponse(coupon);
    }

    /**
     * 쿠폰을 사용하는 유스케이스
     *
     * @param command 사용할려는 쿠폰과 쿠폰코드 및 소유자의 아이디
     * @return 사용완료된 로직과 할인이 얼만큼 적용되는지 알려주는 객체
     */
    @Override
    public CouponUseUpdateResponse useCoupon(@Valid CouponUseUpdateCommand command) {
        Coupon coupon = couponUpdateHandler.useCoupon(command);
        return couponDataMapper.couponToCouponUpdateResponse(coupon);
    }

    /**
     * 만료된 쿠폰을 재사용할 수 있게 하는 메서드 (구현계획은 아직 없음)
     *
     * @param command 만료된 쿠폰에 대한 정보와 유저 정보
     * @return 만료된 쿠폰에 대한 정보
     */
    @Override
    public CouponReactiveUpdateResponse reactiveCoupon(@Valid CouponReactiveUpdateCommand command) {
        return null;
    }

    /**
     * 결제내역에 대한 단건 조회
     *
     * @param command 결제정보와 유저 정보로 결제 내역을 조회하는 객체
     * @return 해당 결제 내역을 바탕으로 결제 내역을 제공하는 객체
     */
    @Override
    public PaymentTrackQueryResponse trackPayment(@Valid PaymentTrackQuery command) {
        Payment payment = couponPaymentHandler.findPaymentByUserIdAndPaymentId(command.getUserId(),
                command.getPaymentId());
        return couponDataMapper.paymentToPaymentTrackQueryResponse(payment);
    }

    @Override
    public CouponCancelUpdateResponse cancelCoupon(@Valid CouponCancelUpdateCommand command) {
        Coupon coupon = couponTrackHandler.findCouponById(
                new CouponTrackQuery(command.getUserId(), command.getCouponId()));
        Payment payment = couponPaymentHandler.findPaymentByUserIdAndCouponId(command.getUserId(), coupon.getId().getValue());
        PaymentResponse refund = paymentTossAPIPort.refund(new PaymentRefundRequest(command.getCancelReason(),
                payment.getPaymentKey().getValue()));
        if (!refund.getStatus().equals(PaymentStatus.CANCELED)) {
            throw new PaymentException("Refund failed");
        }
        Coupon cancelled = couponUpdateHandler.cancelCoupon(coupon);
        Payment refundPayment = couponPaymentHandler.refundPayment(payment, command.getCancelReason());
        return couponDataMapper.couponToCouponCancelUpdateResponse(cancelled, refundPayment);
    }

    @Override
    public CouponUsageTrackQueryResponse trackCouponUsage(CouponUsageTrackQuery command) {
        CouponUsage couponUsage = couponTrackHandler.findCouponUsageByUserIdAndCouponId(command);
        return couponDataMapper.couponToCouponUsageTrackQueryResponse(couponUsage);
    }
}
