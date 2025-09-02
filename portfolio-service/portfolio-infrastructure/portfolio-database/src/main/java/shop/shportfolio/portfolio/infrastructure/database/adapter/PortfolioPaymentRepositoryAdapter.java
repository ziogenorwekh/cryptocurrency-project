package shop.shportfolio.portfolio.infrastructure.database.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.Payment;
import shop.shportfolio.portfolio.infrastructure.database.entity.PaymentEntity;
import shop.shportfolio.portfolio.infrastructure.database.mapper.PortfolioDataAccessMapper;
import shop.shportfolio.portfolio.infrastructure.database.repository.PaymentJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PortfolioPaymentRepositoryAdapter implements PortfolioPaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PortfolioDataAccessMapper portfolioDataAccessMapper;

    @Autowired
    public PortfolioPaymentRepositoryAdapter(PaymentJpaRepository paymentJpaRepository,
                                             PortfolioDataAccessMapper portfolioDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.portfolioDataAccessMapper = portfolioDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = portfolioDataAccessMapper.paymentToPaymentEntity(payment);
        return portfolioDataAccessMapper.paymentEntityToPayment(paymentJpaRepository.save(entity));
    }

    @Override
    public List<Payment> findPaymentsByUserId(UUID userId) {
        return paymentJpaRepository.findPaymentEntitiesByUserId(userId)
                .stream().map(portfolioDataAccessMapper::paymentEntityToPayment).collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId) {
        return paymentJpaRepository.findPaymentEntityByUserIdAndPaymentId(userId,paymentId)
                .map(portfolioDataAccessMapper::paymentEntityToPayment);
    }

}
