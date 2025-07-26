package shop.shportfolio.coupon.infrastructure.database.adapter;

import org.springframework.stereotype.Repository;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.coupon.application.ports.output.repository.CouponPaymentRepositoryPort;
import shop.shportfolio.coupon.infrastructure.database.entity.PaymentEntity;
import shop.shportfolio.coupon.infrastructure.database.mapper.CouponDataAccessMapper;
import shop.shportfolio.coupon.infrastructure.database.repository.PaymentJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CouponPaymentRepositoryAdapter implements CouponPaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final CouponDataAccessMapper couponDataAccessMapper;

    public CouponPaymentRepositoryAdapter(PaymentJpaRepository paymentJpaRepository,
                                          CouponDataAccessMapper couponDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.couponDataAccessMapper = couponDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = couponDataAccessMapper.paymentToPaymentEntity(payment);
        PaymentEntity saved = paymentJpaRepository.save(paymentEntity);
        return couponDataAccessMapper.paymentEntityToPayment(saved);
    }

    @Override
    public List<Payment> findPaymentsByUserId(UUID userId) {
        List<PaymentEntity> entity = paymentJpaRepository.findPaymentEntityByUserId(userId);
        return entity.stream().map(couponDataAccessMapper::paymentEntityToPayment).collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findPaymentByUserIdAndPaymentId(UUID userId, UUID paymentId) {
        Optional<PaymentEntity> entity = paymentJpaRepository.findPaymentEntityByUserIdAndPaymentId(userId, paymentId);
        return entity.map(couponDataAccessMapper::paymentEntityToPayment);
    }

    @Override
    public Optional<Payment> findPaymentByUserIdAndCouponId(UUID paymentId, UUID couponId) {
        Optional<PaymentEntity> entity = paymentJpaRepository.findPaymentEntityByUserIdAndCouponId(paymentId, couponId);
        return entity.map(couponDataAccessMapper::paymentEntityToPayment);
    }
}
