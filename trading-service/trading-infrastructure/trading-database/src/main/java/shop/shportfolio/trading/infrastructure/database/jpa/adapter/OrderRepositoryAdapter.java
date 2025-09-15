package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingOrderDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.LimitOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.MarketOrderJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.ReservationOrderJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryAdapter implements TradingOrderRepositoryPort {


    private final TradingOrderDataAccessMapper mapper;
    private final LimitOrderJpaRepository limitOrderJpaRepository;
    private final ReservationOrderJpaRepository reservationOrderJpaRepository;
    private final MarketOrderJpaRepository marketOrderJpaRepository;

    @Autowired
    public OrderRepositoryAdapter(TradingOrderDataAccessMapper mapper,
                                  LimitOrderJpaRepository limitOrderJpaRepository,
                                  ReservationOrderJpaRepository reservationOrderJpaRepository,
                                  MarketOrderJpaRepository marketOrderJpaRepository) {
        this.mapper = mapper;
        this.limitOrderJpaRepository = limitOrderJpaRepository;
        this.reservationOrderJpaRepository = reservationOrderJpaRepository;
        this.marketOrderJpaRepository = marketOrderJpaRepository;
    }

    @Override
    public Optional<LimitOrder> findLimitOrderByOrderIdAndUserId(String orderId, UUID userId) {
        Optional<LimitOrderEntity> optional = limitOrderJpaRepository
                .findLimitOrderEntityByOrderIdAndUserId(orderId, userId);
        return optional.map(mapper::limitOrderEntityToLimitOrder);
    }

    @Override
    public Optional<ReservationOrder> findReservationOrderByOrderIdAndUserId(String orderId, UUID userId) {
        return reservationOrderJpaRepository
                .findReservationOrderEntityByOrderIdAndUserId(orderId, userId)
                .map(mapper::reservationOrderEntityToReservationOrder);
    }

    @Override
    public LimitOrder saveLimitOrder(LimitOrder limitOrder) {
        LimitOrderEntity limitOrderEntity = mapper.limitOrderEntityToLimitOrderEntity(limitOrder);
        LimitOrderEntity saved = limitOrderJpaRepository.save(limitOrderEntity);
        return mapper.limitOrderEntityToLimitOrder(saved);
    }

    @Override
    public MarketOrder saveMarketOrder(MarketOrder marketOrder) {
        MarketOrderEntity entity = mapper.marketOrderEntityToMarketOrder(marketOrder);
        MarketOrderEntity saved = marketOrderJpaRepository.save(entity);
        return mapper.marketOrderToMarketOrderEntity(saved);
    }

    @Override
    public ReservationOrder saveReservationOrder(ReservationOrder reservationOrder) {
        ReservationOrderEntity entity = mapper.reservationOrderToReservationOrderEntity(reservationOrder);
        ReservationOrderEntity saved = reservationOrderJpaRepository.save(entity);
        return mapper.reservationOrderEntityToReservationOrder(saved);
    }

    @Override
    public Optional<LimitOrder> findLimitOrderByOrderId(String orderId) {
        return limitOrderJpaRepository.findLimitOrderEntityByOrderId(orderId)
                .map(mapper::limitOrderEntityToLimitOrder);
    }

    @Override
    public Optional<MarketOrder> findMarketOrderByOrderId(String orderId) {
        return marketOrderJpaRepository.findMarketOrderEntityByOrderId(orderId)
                .map(mapper::marketOrderToMarketOrderEntity);
    }

    @Override
    public Optional<ReservationOrder> findReservationOrderByOrderId(String orderId) {
        return reservationOrderJpaRepository.findReservationOrderEntityByOrderId(orderId)
                .map(mapper::reservationOrderEntityToReservationOrder);
    }

}
