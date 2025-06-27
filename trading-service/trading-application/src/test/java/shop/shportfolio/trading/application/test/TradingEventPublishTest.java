package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TradingEventPublishTest {
    @Test
    @DisplayName("Kafka 이벤트 발행 횟수 검증")
    void verifyKafkaPublishEvents() {}

    @Test
    @DisplayName("체결 후 Kafka Trade 이벤트 페이로드 정확성 검증")
    void verifyTradeKafkaEventPayload() {}
}
