package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TradingInfrastructureFailureTest {
    @Test
    @DisplayName("Redis 조회 실패 시 예외 처리 테스트")
    public void redisLookupFailureHandling() {

    }

    @Test
    @DisplayName("Redis 장애 발생 시 주문 처리 로직이 안전하게 동작하는지 테스트")
    public void orderProcessingWhenRedisIsDown() {}
}
