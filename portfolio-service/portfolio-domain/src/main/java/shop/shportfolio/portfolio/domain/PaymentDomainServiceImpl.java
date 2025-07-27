package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.Payment;

public class PaymentDomainServiceImpl implements PaymentDomainService {
    @Override
    public Payment createPayment(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount, PaymentMethod paymentMethod, PaymentStatus status, Description description, String rawResponse) {
        return Payment.create(userId, paymentKey, totalAmount,
                paymentMethod, status, description, rawResponse);
    }

    @Override
    public void cancelPayment(Payment payment,String reason) {
        payment.cancel(reason);
    }
}
