package shop.shportfolio.portfolio.infrastructure.toss.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentRefundRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.portfolio.application.exception.TossAPIException;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.infrastructure.toss.mapper.PortfolioDataApiMapper;

import java.time.Duration;

@Slf4j
@Component
public class TossAPIClient implements PaymentTossAPIPort {

    private final WebClient webClient;
    private final PortfolioDataApiMapper portfolioDataApiMapper;

    public TossAPIClient(WebClient tossWebClient,
                         PortfolioDataApiMapper portfolioDataApiMapper) {
        this.webClient = tossWebClient;
        this.portfolioDataApiMapper = portfolioDataApiMapper;
    }

    @Override
    public PaymentResponse refund(PaymentRefundRequest refundRequest) {
        try {
            return webClient.post()
                    .uri(String.format("/%s/cancel", refundRequest.getPaymentKey()))
                    .bodyValue(refundRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                log.error("Toss API refund error: {}", errorBody);
                                return Mono.error(new TossAPIException("환불 처리 중 오류가 발생했습니다."));
                            })
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .map(responseBody -> {
                        log.info("Toss API refund response: {}", responseBody);
                        return portfolioDataApiMapper.toPaymentResponse(responseBody);
                    })
                    .block();
        } catch (Exception e) {
            log.error("Exception during refund call to Toss API", e);
            throw new TossAPIException("환불 처리 중 오류가 발생했습니다.");
        }
    }

    @Override
    public PaymentResponse pay(PaymentPayRequest paymentPayRequest) {
        try {
            return webClient.post()
                    .uri("/confirm")
                    .bodyValue(paymentPayRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                log.error("Toss API pay error: {}", errorBody);
                                return Mono.error(new TossAPIException("결제 처리 중 오류가 발생했습니다."));
                            })
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .map(responseBody -> {
                        log.info("Toss API pay response: {}", responseBody);
                        return portfolioDataApiMapper.toPaymentResponse(responseBody);
                    })
                    .block();

        } catch (Exception e) {
            log.error("Exception during pay call to Toss API", e);
            throw new TossAPIException("결제 처리 중 오류가 발생했습니다.");
        }
    }
}
