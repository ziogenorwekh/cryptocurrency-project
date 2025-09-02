package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioPaymentRepositoryPort {
    Payment save(Payment payment);
    List<Payment> findPaymentsByUserId(UUID userId);
    Optional<Payment> findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId);
}
