package shop.shportfolio.coupon.infrastructure.toss.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.PaymentMethod;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.coupon.application.exception.DataApiMapperException;
import shop.shportfolio.coupon.infrastructure.toss.dto.PaymentTossAPIResponse;

@Slf4j
@Component
public class CouponDataApiMapper {

    private final ObjectMapper objectMapper;

    public CouponDataApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public PaymentResponse toPaymentResponse(String rawResponse) {
        try {
            PaymentTossAPIResponse paymentTossAPIResponse = objectMapper.readValue(rawResponse, PaymentTossAPIResponse.class);
            return paymentTossAPIResponseToPaymentResponse(paymentTossAPIResponse,rawResponse);
        } catch (Exception e) {
            log.error("Failed to map rawResponse to PaymentResponse", e);
            throw new DataApiMapperException("PaymentResponse 매핑 실패", e);
        }
    }

    private PaymentResponse paymentTossAPIResponseToPaymentResponse(PaymentTossAPIResponse paymentTossAPIResponse
    ,String rawResponse) {
        return new PaymentResponse(
                paymentTossAPIResponse.getPaymentKey(),
                paymentTossAPIResponse.getOrderId(),
                paymentTossAPIResponse.getTotalAmount(),
                mapMethod(paymentTossAPIResponse.getMethod()),
                paymentTossAPIResponse.getStatus(),
                paymentTossAPIResponse.getRequestedAt().toLocalDateTime(),
                paymentTossAPIResponse.getApprovedAt().toLocalDateTime(),
                paymentTossAPIResponse.getOrderName(),
                rawResponse
        );
    }

    private PaymentMethod mapMethod(String method) {
        return switch (method) {
            case "카드" -> PaymentMethod.CARD;
            case "간편결제" -> PaymentMethod.EASY_PAY;
            default -> throw new DataApiMapperException("Unknown payment method: " + method);
        };
    }
}
