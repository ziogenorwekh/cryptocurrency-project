package shop.shportfolio.coupon.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.coupon.application.exception.PaymentNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponPaymentRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CouponPaymentHandler {

    private final CouponPaymentRepositoryPort paymentRepositoryPort;
    private final CouponDomainService couponDomainService;

    @Autowired
    public CouponPaymentHandler(CouponPaymentRepositoryPort paymentRepositoryPort, CouponDomainService couponDomainService) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.couponDomainService = couponDomainService;
    }


    public void save(UserId userId, PaymentResponse paymentResponse, CouponId couponId) {
        Payment payment = couponDomainService.createPayment(userId,
                couponId,
                new PaymentKey(paymentResponse.getPaymentKey()),
                new OrderPrice(BigDecimal.valueOf(paymentResponse.getTotalAmount())),
                paymentResponse.getMethod(), paymentResponse.getStatus(),
                new Description(paymentResponse.getDescription()),
                paymentResponse.getRawResponse());
        paymentRepositoryPort.save(payment);
    }

    public List<Payment> findPaymentsByUserId(UUID userId) {
        return paymentRepositoryPort.findPaymentsByUserId(userId);
    }

    public Payment findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId) {
        return paymentRepositoryPort.findPaymentByUserIdAndPaymentId(userId, paymentId).orElseThrow(
                () -> new PaymentNotFoundException(String.format("Payment not found for id: %s", paymentId)));
    }

    public Payment findPaymentByUserIdAndCouponId(UUID userId, UUID couponId) {
        return paymentRepositoryPort.findPaymentByUserIdAndCouponId(userId, couponId).
                orElseThrow(() ->
                        new PaymentNotFoundException(String.format("Payment not found for coupon id: %s", couponId)));
    }

    public Payment refundPayment(Payment payment, String reason) {
        Payment refundPayment = couponDomainService.refundPayment(payment, reason);
        log.info("refund id : {} status : {} reason : {}", payment.getId().getValue(),
                refundPayment.getStatus().name(), reason);
        return paymentRepositoryPort.save(refundPayment);
    }
}
