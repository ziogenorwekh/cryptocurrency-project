package shop.shportfolio.trading.infrastructure.redis.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.infrastructure.redis.order.OrderMemoryStore;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradingOrderRedisAdapter implements TradingOrderRedisPort {

    private final RedisTemplate<String, String> redisTemplate; // Redis에는 키만 저장
    private final OrderMemoryStore memoryStore;
    @Autowired
    public TradingOrderRedisAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        memoryStore = OrderMemoryStore.getInstance();
    }

    /* ------------------ LIMIT ORDER ------------------ */
    @Override
    public void saveLimitOrder(String key, LimitOrder limitOrder) {
        // Redis에는 키만 저장
        redisTemplate.opsForValue().set(key, "1");
        // 인메모리에 객체 저장
        memoryStore.getLimitOrders().add(limitOrder);
    }

    @Override
    public List<LimitOrder> findLimitOrdersByMarketId(String marketId) {
        // 인메모리 큐에서 직접 꺼냄 (Redis를 거칠 필요 없음)
        return memoryStore.getLimitOrders()
                .stream()
                .filter(o -> o.getMarketId().getValue().equals(marketId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLimitOrder(String key) {
        redisTemplate.delete(key);
        memoryStore.getLimitOrders().removeIf(order ->
                (RedisKeyPrefix.limit(order.getMarketId().getValue(), order.getId().getValue())).equals(key));
    }

    /* ------------------ RESERVATION ORDER ------------------ */
    @Override
    public void saveReservationOrder(String key, ReservationOrder reservationOrder) {
        redisTemplate.opsForValue().set(key, "1");
        memoryStore.getReservationOrders().add(reservationOrder);
    }

    @Override
    public List<ReservationOrder> findReservationOrdersByMarketId(String marketId) {
        return memoryStore.getReservationOrders()
                .stream()
                .filter(o -> o.getMarketId().getValue().equals(marketId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReservationOrder(String key) {
        redisTemplate.delete(key);
        memoryStore.getReservationOrders().removeIf(order ->
                (RedisKeyPrefix.reservation(order.getMarketId().getValue(),
                        order.getId().getValue())).equals(key));
    }

    @Override
    public void saveMarketOrder(String key, MarketOrder marketOrder) {
        redisTemplate.opsForValue().set(key, "1");
        memoryStore.getMarketOrders().add(marketOrder);
    }

    @Override
    public List<MarketOrder> findMarketOrdersByMarketId(String marketId) {
        return memoryStore.getMarketOrders()
                .stream()
                .filter(o->o.getMarketId().getValue().equals(marketId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMarketOrder(String key) {
        redisTemplate.delete(key);
        memoryStore.getMarketOrders().removeIf(order->RedisKeyPrefix.market(order.getMarketId().getValue(),
                order.getId().getValue()).equals(key));
    }

//    private final RedisTemplate<String, LimitOrder> limitOrderRedisTemplate;
//    private final RedisTemplate<String, ReservationOrder> reservationOrderRedisTemplate;
//
//    @Autowired
//    public TradingOrderRedisAdapter(RedisTemplate<String, LimitOrder> limitOrderRedisTemplate,
//                                    RedisTemplate<String, ReservationOrder> reservationOrderRedisTemplate) {
//        this.limitOrderRedisTemplate = limitOrderRedisTemplate;
//        this.reservationOrderRedisTemplate = reservationOrderRedisTemplate;
//    }
//
//    @Override
//    public void saveLimitOrder(String key, LimitOrder limitOrder) {
//        limitOrderRedisTemplate.opsForValue().set(key, limitOrder);
//    }
//
//    @Override
//    public List<LimitOrder> findLimitOrdersByMarketId(String marketId) {
//        String pattern = RedisKeyPrefix.LIMIT_PREFIX + ":" + marketId + ":*";
//
//        Set<String> keys = scanKeys(limitOrderRedisTemplate, pattern);
//        return limitOrderRedisTemplate.opsForValue()
//                .multiGet(keys)
//                .stream()
//                .filter(Objects::nonNull)
//                .toList();
//    }
//
//    @Override
//    public void deleteLimitOrder(String key) {
//        limitOrderRedisTemplate.delete(key);
//    }
//
//    @Override
//    public void saveReservationOrder(String key, ReservationOrder reservationOrder) {
//        reservationOrderRedisTemplate.opsForValue().set(key, reservationOrder);
//    }
//
//    @Override
//    public List<ReservationOrder> findReservationOrdersByMarketId(String marketId) {
//        String pattern = RedisKeyPrefix.RESERVED_PREFIX + ":" + marketId + ":*";
//        Set<String> keys = scanKeys(reservationOrderRedisTemplate, pattern);
//        return reservationOrderRedisTemplate.opsForValue()
//                .multiGet(keys)
//                .stream()
//                .filter(Objects::nonNull)
//                .toList();
//    }
//
//    @Override
//    public void deleteReservationOrder(String key) {
//        reservationOrderRedisTemplate.delete(key);
//    }
//
//    private Set<String> scanKeys(RedisTemplate<?, ?> redisTemplate, String pattern) {
//        return redisTemplate.execute(connection -> {
//            Set<String> keys = new HashSet<>();
//            Cursor<byte[]> cursor = connection.scan(
//                    ScanOptions.scanOptions().match(pattern).count(1000).build()
//            );
//            cursor.forEachRemaining(key -> keys.add(new String(key)));
//            return keys;
//        }, false);
//    }
}
