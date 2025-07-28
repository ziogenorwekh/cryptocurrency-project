package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;

@Getter
@AllArgsConstructor
public class DepositResultContext {

    private final DepositCreatedEvent depositCreatedEvent;
    private final CurrencyBalance balance;
}
