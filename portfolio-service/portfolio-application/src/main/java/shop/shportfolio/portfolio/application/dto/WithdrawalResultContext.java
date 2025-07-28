package shop.shportfolio.portfolio.application.dto;

import lombok.Getter;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;

@Getter
public class WithdrawalResultContext {

    private final WithdrawalCreatedEvent withdrawalCreatedEvent;
    private final CurrencyBalance balance;

    public WithdrawalResultContext(WithdrawalCreatedEvent withdrawalCreatedEvent, CurrencyBalance balance) {
        this.withdrawalCreatedEvent = withdrawalCreatedEvent;
        this.balance = balance;
    }
}
