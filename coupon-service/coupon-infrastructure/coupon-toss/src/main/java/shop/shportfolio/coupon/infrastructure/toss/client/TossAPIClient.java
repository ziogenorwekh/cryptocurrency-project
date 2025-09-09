package shop.shportfolio.coupon.infrastructure.toss.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentRefundRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.coupon.application.exception.TossAPIException;
import shop.shportfolio.coupon.application.ports.output.payment.PaymentTossAPIPort;
import shop.shportfolio.coupon.infrastructure.toss.config.TossPaymentKeyData;
import shop.shportfolio.coupon.infrastructure.toss.config.URIConfigData;
import shop.shportfolio.coupon.infrastructure.toss.mapper.CouponDataApiMapper;

import java.time.Duration;

@Component
public class TossAPIClient implements PaymentTossAPIPort {

    private final WebClient webClient;
    private final URIConfigData uriConfigData;
    private final CouponDataApiMapper couponDataApiMapper;
    private final TossPaymentKeyData tossPaymentKeyData;

    public TossAPIClient(WebClient tossWebClient, URIConfigData uriConfigData,
                         CouponDataApiMapper couponDataApiMapper,
                         TossPaymentKeyData tossPaymentKeyData) {
        this.webClient = tossWebClient;
        this.uriConfigData = uriConfigData;
        this.couponDataApiMapper = couponDataApiMapper;
        this.tossPaymentKeyData = tossPaymentKeyData;
    }

    @Override
    public PaymentResponse refund(PaymentRefundRequest refundRequest) {
        return webClient.post()
                .uri(uriConfigData.getRefund())
                .headers(headers -> headers.setBasicAuth(tossPaymentKeyData.getSecretKey(), ""))
                .bodyValue(refundRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new TossAPIException(
                                        String.format("toss api error is : %s", errorBody)))))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(couponDataApiMapper::toPaymentResponse)
                .block();
    }

    @Override
    public PaymentResponse pay(PaymentPayRequest paymentPayRequest) {
        return webClient.post()
                .uri(uriConfigData.getConfirm())
                .headers(headers -> headers.setBasicAuth(tossPaymentKeyData.getSecretKey(), ""))
                .bodyValue(paymentPayRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new TossAPIException(
                                        String.format("toss api error is : %s", errorBody)))))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(uriConfigData.getTimeout()))
                .map(couponDataApiMapper::toPaymentResponse)
                .block();
    }
}
