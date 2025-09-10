package shop.shportfolio.apigateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import shop.shportfolio.apigateway.filter.config.InMemoryRateLimiter;

@Order(1)
@Component("RateLimitingFilter")
public class RateLimitingFilter extends AbstractGatewayFilterFactory<Object> {

    private final InMemoryRateLimiter rateLimiter;


    @Autowired
    public RateLimitingFilter(InMemoryRateLimiter rateLimiter) {
        super(Object.class);
        this.rateLimiter = rateLimiter;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // 사용자 고유 키 (IP, UserID 등) 추출
            String userKey = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

            if (!rateLimiter.isAllowed(userKey)) {
                return Mono.fromRunnable(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                    exchange.getResponse().setComplete();
                });
            }

            return chain.filter(exchange);
        };
    }
}