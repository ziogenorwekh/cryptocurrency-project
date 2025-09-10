package shop.shportfolio.coupon.infrastructure.database.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponUsageEntity;
import shop.shportfolio.coupon.infrastructure.database.mapper.CouponDataAccessMapper;
import shop.shportfolio.coupon.infrastructure.database.repository.CouponJpaRepository;
import shop.shportfolio.coupon.infrastructure.database.repository.CouponUsageJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CouponRepositoryAdapter implements CouponRepositoryPort {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponDataAccessMapper couponDataAccessMapper;
    private final CouponUsageJpaRepository couponUsageJpaRepository;

    @Autowired
    public CouponRepositoryAdapter(CouponJpaRepository couponJpaRepository,
                                   CouponDataAccessMapper couponDataAccessMapper,
                                   CouponUsageJpaRepository couponUsageJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponDataAccessMapper = couponDataAccessMapper;
        this.couponUsageJpaRepository = couponUsageJpaRepository;
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

    @Override
    public CouponUsage saveCouponUsage(CouponUsage couponUsage) {
        CouponEntity couponEntity = couponJpaRepository.findCouponEntityByUserIdAndCouponId(couponUsage.getUserId().getValue(),
                couponUsage.getCouponId().getValue()).orElseThrow(() -> new CouponNotFoundException(
                String.format("Coupon %s Id is not found", couponUsage.getCouponId().getValue())));
        CouponUsageEntity couponUsageEntity = couponDataAccessMapper
                .couponUsageToCouponUsageEntity(couponUsage, couponEntity);
        CouponUsageEntity saved = couponUsageJpaRepository.save(couponUsageEntity);
        return couponDataAccessMapper.couponUsageEntityToCouponUsage(saved);
    }

    @Override
    public Optional<CouponUsage> findCouponUsageByUserIdAndCouponId(UUID userId, UUID couponId) {
        Optional<CouponUsageEntity> entity = couponUsageJpaRepository.
                findCouponUsageEntityByUserIdAndCouponEntity_CouponId(userId, couponId);
        return entity.map(couponDataAccessMapper::couponUsageEntityToCouponUsage);
    }

    @Override
    public List<Coupon> findCouponByExpiredDate(LocalDate today) {
        return couponUsageJpaRepository.findExpiredCoupons(today).stream()
                .map(couponDataAccessMapper::couponEntityToCoupon).collect(Collectors.toList());
    }

    @Override
    public void removeCouponUsageByCouponIdAndUserId(UUID couponId,UUID userId) {
        couponUsageJpaRepository.
                removeCouponUsageEntityByCouponEntity_CouponIdAndUserId(couponId, userId);
    }
}
