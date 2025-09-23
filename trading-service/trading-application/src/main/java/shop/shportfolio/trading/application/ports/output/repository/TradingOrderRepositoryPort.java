package shop.shportfolio.trading.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.domain.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradingOrderRepositoryPort {


    Optional<LimitOrder> findLimitOrderByOrderIdAndUserId(String orderId, UUID userId);

    Optional<ReservationOrder> findReservationOrderByOrderIdAndUserId(String orderId, UUID userId);

    LimitOrder saveLimitOrder(LimitOrder limitOrder);

    MarketOrder saveMarketOrder(MarketOrder marketOrder);

    ReservationOrder saveReservationOrder(ReservationOrder reservationOrder);

    Optional<LimitOrder> findLimitOrderByOrderId(String orderId);

    Optional<MarketOrder> findMarketOrderByOrderId(String orderId);

    Optional<ReservationOrder> findReservationOrderByOrderId(String orderId);

    List<Order> findOrderByUserIdAndMarketId(UUID userId, String marketId);
}
