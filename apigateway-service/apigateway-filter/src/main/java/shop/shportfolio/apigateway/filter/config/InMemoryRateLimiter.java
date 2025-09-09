package shop.shportfolio.apigateway.filter.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRateLimiter {
    private final Map<String, RateLimiterData> rateLimiters = new ConcurrentHashMap<>();
    private final int requestLimit = 5; // 허용 요청 수 (초당)
    private final long timeWindow = 1000; // 시간 창 (밀리초 단위)

    public boolean isAllowed(String key) {
        RateLimiterData data = rateLimiters.computeIfAbsent(key, k -> new RateLimiterData());

        synchronized (data) {
            long currentTime = Instant.now().toEpochMilli();

            // 시간 창이 만료되었는지 확인
            if (currentTime - data.startTime > timeWindow) {
                data.startTime = currentTime;
                data.requestCount = 0;
            }

            // 요청 횟수 초과 여부 확인
            if (data.requestCount >= requestLimit) {
                return false; // Rate limit exceeded
            }

            // 요청 카운트 증가
            data.requestCount++;
            return true;
        }
    }

    // 요청 데이터를 담는 클래스
    private static class RateLimiterData {
        long startTime = Instant.now().toEpochMilli();
        int requestCount = 0;
    }
}