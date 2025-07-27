package shop.shportfolio.portfolio.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.Description;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.PaymentKey;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.domain.PaymentDomainService;
import shop.shportfolio.portfolio.domain.entity.Payment;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PortfolioPaymentHandler {

    private final PaymentTossAPIPort paymentTossAPIPort;
    private final PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;

    @Autowired
    public PortfolioPaymentHandler(PaymentTossAPIPort paymentTossAPIPort,
                                   PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort,
                                   PaymentDomainService paymentDomainService) {
        this.paymentTossAPIPort = paymentTossAPIPort;
        this.portfolioPaymentRepositoryPort = portfolioPaymentRepositoryPort;
        this.paymentDomainService = paymentDomainService;
    }

    public PaymentResponse pay(PaymentPayRequest request) {
        return paymentTossAPIPort.pay(request);
    }

    public Payment create(UUID userId, PaymentResponse response) {
        Payment payment = paymentDomainService.createPayment(new UserId(userId), new PaymentKey(response.getPaymentKey()),
                OrderPrice.of(BigDecimal.valueOf(response.getTotalAmount())), response.getMethod(), response.getStatus(),
                new Description(response.getDescription()),
                response.getRawResponse());
        return portfolioPaymentRepositoryPort.save(payment);
    }
}
