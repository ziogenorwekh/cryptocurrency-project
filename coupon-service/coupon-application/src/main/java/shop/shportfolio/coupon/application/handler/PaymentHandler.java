package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.Description;
import shop.shportfoilo.coupon.domain.valueobject.PaidAt;
import shop.shportfoilo.coupon.domain.valueobject.PaymentKey;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.coupon.application.dto.payment.PaymentResponse;
import shop.shportfolio.coupon.application.exception.PaymentNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.PaymentRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class PaymentHandler {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final CouponDomainService couponDomainService;

    @Autowired
    public PaymentHandler(PaymentRepositoryPort paymentRepositoryPort, CouponDomainService couponDomainService) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.couponDomainService = couponDomainService;
    }


    public Payment save(PaymentResponse  paymentResponse) {
        Payment payment = couponDomainService.createPayment(new UserId(UUID.fromString(paymentResponse.getUserId())),
                new PaymentKey(paymentResponse.getPaymentKey()),
                new OrderPrice(BigDecimal.valueOf(paymentResponse.getTotalAmount())),
                paymentResponse.getMethod(), paymentResponse.getStatus(),
                new CreatedAt(paymentResponse.getRequestedAt()), new PaidAt(paymentResponse.getPaidAt()),
                new Description(paymentResponse.getDescription()),
                paymentResponse.getRawResponse());
        return paymentRepositoryPort.save(payment);
    }

    public List<Payment> findPaymentsByUserId(UUID userId) {
        return paymentRepositoryPort.findPaymentsByUserId(userId);
    }

    public Payment findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId) {
        return paymentRepositoryPort.findPaymentByUserIdAndPaymentId(userId, paymentId).orElseThrow(() -> {
            throw new PaymentNotFoundException(String.format("Payment not found for id: %s", paymentId));
        });
    }
}
