package shop.shportfolio.trading.application.ports.output.redis;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface TradingOrderRedisPort {

    void saveLimitOrder(String key, LimitOrder limitOrder);

    // 중요: findLimitOrdersByMarketId 내부에서 MGET 혹은 파이프라이닝으로 일괄 조회 구현 전제
    List<LimitOrder> findLimitOrdersByMarketId(String marketId);

    void deleteLimitOrder(String key);

    void saveReservationOrder(String key, ReservationOrder reservationOrder);

    void deleteReservationOrder(String key);
}
