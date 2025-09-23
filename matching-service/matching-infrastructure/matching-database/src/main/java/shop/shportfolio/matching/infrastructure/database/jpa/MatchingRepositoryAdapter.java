package shop.shportfolio.matching.infrastructure.database.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.matching.application.dto.order.OrderContext;
import shop.shportfolio.matching.application.ports.output.repository.MatchingRepository;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.OrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingOrderDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.OrderJpaRepository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class MatchingRepositoryAdapter implements MatchingRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final TradingOrderDataAccessMapper mapper;
    public MatchingRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                     TradingOrderDataAccessMapper mapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public OrderContext findAllOrders() {
        List<ReservationOrder> reservationOrders = new ArrayList<>();
        List<LimitOrder> limitOrders = new ArrayList<>();
        List<MarketOrder> marketOrders = new ArrayList<>();
        List<OrderEntity> entityList = orderJpaRepository.findOrderEntitiesByOrderStatusInOrderByCreatedAtAsc(
                List.of(OrderStatus.OPEN, OrderStatus.PARTIALLY_FILLED)
        );
        entityList.forEach(orderEntity -> {
            switch (orderEntity.getOrderType()) {
                case RESERVATION -> reservationOrders.add((ReservationOrder) mapper.orderEntityToOrder(orderEntity));
                case LIMIT -> limitOrders.add((LimitOrder) mapper.orderEntityToOrder(orderEntity));
                case MARKET -> marketOrders.add((MarketOrder) mapper.orderEntityToOrder(orderEntity));
            }
        });
        return new OrderContext(limitOrders, reservationOrders, marketOrders);
    }
}
