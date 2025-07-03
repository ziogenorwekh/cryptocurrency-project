package shop.shportfolio.coupon.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.*;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponReactiveUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.dto.payment.PaymentRequest;
import shop.shportfolio.coupon.application.dto.payment.PaymentResponse;
import shop.shportfoilo.coupon.domain.valueobject.PaymentStatus;
import shop.shportfolio.coupon.application.exception.PaymentException;
import shop.shportfolio.coupon.application.handler.CouponCreateHandler;
import shop.shportfolio.coupon.application.handler.CouponTrackHandler;
import shop.shportfolio.coupon.application.handler.CouponUpdateHandler;
import shop.shportfolio.coupon.application.handler.PaymentHandler;
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
    private final PaymentHandler paymentHandler;

    @Autowired
    public CouponApplicationServiceImpl(CouponCreateHandler couponCreateHandler,
                                        CouponDataMapper couponDataMapper,
                                        CouponTrackHandler couponTrackHandler,
                                        CouponUpdateHandler couponUpdateHandler,
                                        PaymentTossAPIPort paymentTossAPIPort,
                                        PaymentHandler paymentHandler) {
        this.couponCreateHandler = couponCreateHandler;
        this.couponDataMapper = couponDataMapper;
        this.couponTrackHandler = couponTrackHandler;
        this.couponUpdateHandler = couponUpdateHandler;
        this.paymentTossAPIPort = paymentTossAPIPort;
        this.paymentHandler = paymentHandler;
    }

    @Override
    public CouponCreatedResponse createCoupon(CouponCreateCommand command) {
        PaymentResponse paymentResponse = paymentTossAPIPort.pay(couponDataMapper.
                couponCreateCommandToPaymentRequest(command));
        log.info("paymentResponse -> {}", paymentResponse.toString());
        if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
            Coupon coupon = couponCreateHandler.createCoupon(command);
            Payment saved = paymentHandler.save(paymentResponse);
            return couponDataMapper.couponToCouponCreatedResponse(coupon);
        }
        throw new PaymentException("Payment failed");
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
        Coupon coupon = couponUpdateHandler.useCoupon(command);
        return couponDataMapper.couponToCouponUpdateResponse(coupon);
    }

    @Override
    public CouponReactiveUpdateResponse reactiveCoupon(CouponReactiveUpdateCommand command) {
        return null;
    }

    @Override
    public PaymentTrackQueryResponse trackPayment(PaymentTrackQuery command) {
        Payment payment = paymentHandler.findPaymentByUserIdAndPaymentId(command.getUserId(),
                command.getPaymentId());
        return couponDataMapper.paymentToPaymentTrackQueryResponse(payment);
    }
}
