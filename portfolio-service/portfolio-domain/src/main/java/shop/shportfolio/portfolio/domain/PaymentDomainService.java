package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.Payment;

public interface PaymentDomainService {

    Payment createPayment(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                          PaymentMethod paymentMethod, PaymentStatus status,
                          Description description, String rawResponse);

    void cancelPayment(Payment payment, String reason);

}
