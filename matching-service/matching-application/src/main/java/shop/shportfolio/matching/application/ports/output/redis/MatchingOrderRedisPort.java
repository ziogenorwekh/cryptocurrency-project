package shop.shportfolio.matching.application.ports.output.redis;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface MatchingOrderRedisPort {

    void saveLimitOrder(String key, LimitOrder limitOrder);

    // 중요: findLimitOrdersByMarketId 내부에서 MGET 혹은 파이프라이닝으로 일괄 조회 구현 전제
    List<LimitOrder> findLimitOrdersByMarketId(String marketId);

    void deleteLimitOrder(String key);

    void saveReservationOrder(String key, ReservationOrder reservationOrder);

    List<ReservationOrder> findReservationOrdersByMarketId(String marketId);

    void deleteReservationOrder(String key);

    void saveMarketOrder(String key, MarketOrder marketOrder);

    List<MarketOrder> findMarketOrdersByMarketId(String marketId);

    void deleteMarketOrder(String key);
}
