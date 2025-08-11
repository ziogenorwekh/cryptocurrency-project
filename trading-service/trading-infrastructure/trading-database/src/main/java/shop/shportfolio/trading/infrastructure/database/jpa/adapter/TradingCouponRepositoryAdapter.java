package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon.CouponInfoEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon.QCouponInfoEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.QTradeEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.TradeEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingCouponInfoDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.CouponInfoJpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TradingCouponRepositoryAdapter implements TradingCouponRepositoryPort {

    private final JPAQueryFactory jpaQueryFactory;
    private final TradingCouponInfoDataAccessMapper mapper;
    private final CouponInfoJpaRepository couponInfoJpaRepository;

    @Autowired
    public TradingCouponRepositoryAdapter(JPAQueryFactory jpaQueryFactory,
                                          TradingCouponInfoDataAccessMapper mapper,
                                          CouponInfoJpaRepository couponInfoJpaRepository) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.mapper = mapper;
        this.couponInfoJpaRepository = couponInfoJpaRepository;
    }

    @Override
    public CouponInfo saveCouponInfo(CouponInfo couponInfo) {
        CouponInfoEntity couponInfoEntity = mapper.couponInfoToCouponInfoEntity(couponInfo);
        return mapper.couponInfoEntityToCouponInfo(couponInfoJpaRepository.save(couponInfoEntity));
    }

    @Override
    public Optional<CouponInfo> findCouponInfoByUserId(UUID userId) {
        Optional<CouponInfoEntity> optional = couponInfoJpaRepository.findCouponInfoEntityByUserId(userId);
        return optional.map(mapper::couponInfoEntityToCouponInfo);
    }

    @Override
    public void deleteCouponInfoByUserId(CouponInfo couponInfo) {
        CouponInfoEntity couponInfoEntity = mapper.couponInfoToCouponInfoEntity(couponInfo);
        couponInfoJpaRepository.delete(couponInfoEntity);
    }

    @Override
    public void deleteAllExpiredCoupons() {
        QCouponInfoEntity couponInfoEntity = QCouponInfoEntity.couponInfoEntity;
        jpaQueryFactory.delete(couponInfoEntity)
                .where(couponInfoEntity.usageExpiryDate.
                        before(LocalDate.now(ZoneOffset.UTC))).execute();
    }
}
