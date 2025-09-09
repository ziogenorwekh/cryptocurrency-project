package shop.shportfolio.apigateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shop.shportfolio.apigateway.filter.config.TokenSecretConfigData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<TokenSecretConfigData> {

    @Autowired
    public AuthenticationFilter(TokenSecretConfigData tokenSecretConfigData) {
        super(TokenSecretConfigData.class);
    }

    @Override
    public GatewayFilter apply(TokenSecretConfigData config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // Authorization 헤더 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing.", HttpStatus.UNAUTHORIZED);
            }
            List<String> authorizations = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authorizations == null || authorizations.isEmpty() || !authorizations.get(0).startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization header.", HttpStatus.UNAUTHORIZED);
            }
            String token = authorizations.get(0).substring("Bearer ".length());
            log.info("token: {}", token);
            if (token.isEmpty()) {
                log.info("Token is empty");
                log.warn("Authorization header is missing.");
                return onError(exchange, "Invalid Token.", HttpStatus.UNAUTHORIZED);
            }
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(config.getSecret()))
                        .build()
                        .verify(token);
                String userId = decodedJWT.getIssuer();
                if (userId == null || userId.isEmpty()) {
                    log.warn("User ID is missing or empty in token.");
                    userId = "";
                }
                List<String> roles = decodedJWT.getClaim("Roles").asList(String.class);
                if (roles == null) {
                    log.warn("Roles claim is null in token.");
                    roles = new ArrayList<>();
                }
                log.info("userId: {}", userId);
                log.info("roles: {}", roles.size());
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-header-User-Id", userId)
                        .header("X-header-User-Roles", String.join(",", roles))
                        .header("X-header-Token", token)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (JWTDecodeException | AlgorithmMismatchException | SignatureVerificationException
                     | TokenExpiredException | NullPointerException e) {
                log.error("class.type is -> {}, Token error is -> {}", e.getClass(), e.getMessage());
                return onError(exchange, "Invalid or expired token.", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(errMessage); // 내부 로그에만 에러 메시지 기록
        response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Authentication failed."); // 외부 클라이언트용 메시지
        return response.setComplete();
    }
}
