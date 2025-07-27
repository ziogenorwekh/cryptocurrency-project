package shop.shportfolio.portfolio.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;

@Component
public class PaymentHandler {

    private final PaymentTossAPIPort paymentTossAPIPort;
    private final PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort;

    @Autowired
    public PaymentHandler(PaymentTossAPIPort paymentTossAPIPort,
                          PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort) {
        this.paymentTossAPIPort = paymentTossAPIPort;
        this.portfolioPaymentRepositoryPort = portfolioPaymentRepositoryPort;
    }

    public PaymentResponse pay(PaymentPayRequest request) {
        return paymentTossAPIPort.pay(request);
    }
}
