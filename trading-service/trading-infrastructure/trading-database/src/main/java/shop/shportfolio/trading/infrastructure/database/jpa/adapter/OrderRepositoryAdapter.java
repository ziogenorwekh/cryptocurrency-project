package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingOrderDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.LimitOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.MarketOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.ReservationOrderJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrderRepositoryAdapter implements TradingOrderRepositoryPort {


    private final JPAQueryFactory jpaQueryFactory;
    private final TradingOrderDataAccessMapper mapper;
    private final LimitOrderJpaRepository limitOrderJpaRepository;
    private final ReservationOrderJpaRepository reservationOrderJpaRepository;
    private final MarketOrderJpaRepository marketOrderJpaRepository;

    @Autowired
    public OrderRepositoryAdapter(JPAQueryFactory jpaQueryFactory,
                                  TradingOrderDataAccessMapper mapper,
                                  LimitOrderJpaRepository limitOrderJpaRepository,
                                  ReservationOrderJpaRepository reservationOrderJpaRepository,
                                  MarketOrderJpaRepository marketOrderJpaRepository) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.mapper = mapper;
        this.limitOrderJpaRepository = limitOrderJpaRepository;
        this.reservationOrderJpaRepository = reservationOrderJpaRepository;
        this.marketOrderJpaRepository = marketOrderJpaRepository;
    }

    @Override
    public Optional<LimitOrder> findLimitOrderByOrderIdAndUserId(String orderId, UUID userId) {
//        QLimitOrderEntity limitOrder = QLi

        return Optional.empty();
    }

    @Override
    public Optional<ReservationOrder> findReservationOrderByOrderIdAndUserId(String orderId, UUID userId) {
        return Optional.empty();
    }

    @Override
    public LimitOrder saveLimitOrder(LimitOrder limitOrder) {
        return null;
    }

    @Override
    public MarketOrder saveMarketOrder(MarketOrder marketOrder) {
        return null;
    }

    @Override
    public ReservationOrder saveReservationOrder(ReservationOrder reservationOrder) {
        return null;
    }
}
