package shop.shportfolio.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<Object> {


    public LoggingFilter() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
//            log.info("Incoming request: Method = {}, Path = {}, Headers = {}",
//                    request.getMethod(), request.getURI(), request.getHeaders());
            log.info("Incoming request: Method = {}, Path = {}",
                    request.getMethod(), request.getURI());
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> log.info("Outgoing response: Status = {}",
                            exchange.getResponse().getStatusCode()));
        };
    }
}