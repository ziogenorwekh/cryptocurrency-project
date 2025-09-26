package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;

@Getter
@AllArgsConstructor
public class DepositResultContext {

    private final DepositCreatedEvent depositCreatedEvent;
    private final CurrencyBalance balance;
    private final PaymentResponse paymentResponse;

}
