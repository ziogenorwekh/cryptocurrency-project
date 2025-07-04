package shop.shportfolio.coupon.infrastructure.database.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;
import shop.shportfolio.coupon.infrastructure.database.mapper.CouponDataAccessMapper;
import shop.shportfolio.coupon.infrastructure.database.repository.CouponJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CouponRepositoryAdapter implements CouponRepositoryPort {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponDataAccessMapper couponDataAccessMapper;

    @Autowired
    public CouponRepositoryAdapter(CouponJpaRepository couponJpaRepository,
                                   CouponDataAccessMapper couponDataAccessMapper) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponDataAccessMapper = couponDataAccessMapper;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity couponEntity = couponDataAccessMapper.couponToCouponEntity(coupon);
        CouponEntity saved = couponJpaRepository.save(couponEntity);
        return couponDataAccessMapper.couponEntityToCoupon(saved);
    }

    @Override
    public List<Coupon> findByUserId(UUID userId) {
        List<CouponEntity> couponEntities = couponJpaRepository.findCouponEntityByUserId(userId);
        return couponEntities.stream().map(couponDataAccessMapper::couponEntityToCoupon)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Coupon> findByUserIdAndCouponId(UUID userId, UUID couponId) {
        Optional<CouponEntity> entity = couponJpaRepository.findCouponEntityByUserIdAndCouponId(userId, couponId);
        return entity.map(couponDataAccessMapper::couponEntityToCoupon);
    }
}
