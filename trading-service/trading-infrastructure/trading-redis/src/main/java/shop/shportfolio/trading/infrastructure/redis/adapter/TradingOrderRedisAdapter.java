package shop.shportfolio.trading.infrastructure.redis.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class TradingOrderRedisAdapter implements TradingOrderRedisPort {

    private final RedisTemplate<String, LimitOrder> limitOrderRedisTemplate;
    private final RedisTemplate<String, ReservationOrder> reservationOrderRedisTemplate;

    @Autowired
    public TradingOrderRedisAdapter(RedisTemplate<String, LimitOrder> limitOrderRedisTemplate, RedisTemplate<String, ReservationOrder> reservationOrderRedisTemplate) {
        this.limitOrderRedisTemplate = limitOrderRedisTemplate;
        this.reservationOrderRedisTemplate = reservationOrderRedisTemplate;
    }


    @Override
    public void saveLimitOrder(String key, LimitOrder limitOrder) {
        limitOrderRedisTemplate.opsForValue().set(key, limitOrder);
    }

    @Override
    public List<LimitOrder> findLimitOrdersByMarketId(String marketId) {
        String pattern = RedisKeyPrefix.LIMIT_PREFIX + ":" + marketId + ":*";

        Set<String> keys = scanKeys(limitOrderRedisTemplate, pattern);
        return limitOrderRedisTemplate.opsForValue()
                .multiGet(keys)
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteLimitOrder(String key) {
        limitOrderRedisTemplate.delete(key);
    }

    @Override
    public void saveReservationOrder(String key, ReservationOrder reservationOrder) {
        reservationOrderRedisTemplate.opsForValue().set(key, reservationOrder);
    }

    @Override
    public List<ReservationOrder> findReservationOrdersByMarketId(String marketId) {
        String pattern = RedisKeyPrefix.RESERVED_PREFIX + ":" + marketId + ":*";
        Set<String> keys = scanKeys(reservationOrderRedisTemplate, pattern);
        return reservationOrderRedisTemplate.opsForValue()
                .multiGet(keys)
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteReservationOrder(String key) {
        reservationOrderRedisTemplate.delete(key);
    }

    private Set<String> scanKeys(RedisTemplate<?, ?> redisTemplate, String pattern) {
        return redisTemplate.execute(connection -> {
            Set<String> keys = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(1000).build()
            );
            cursor.forEachRemaining(key -> keys.add(new String(key)));
            return keys;
        }, false);
    }
}
