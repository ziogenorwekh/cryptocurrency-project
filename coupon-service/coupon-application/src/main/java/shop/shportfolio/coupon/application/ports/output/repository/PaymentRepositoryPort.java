package shop.shportfolio.coupon.application.ports.output.repository;

import shop.shportfoilo.coupon.domain.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);

    List<Payment> findPaymentsByUserId(UUID userId);

    Optional<Payment> findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId);
}
