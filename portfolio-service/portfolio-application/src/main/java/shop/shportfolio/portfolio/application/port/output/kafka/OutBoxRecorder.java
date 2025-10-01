package shop.shportfolio.portfolio.application.port.output.kafka;

import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;

public interface OutBoxRecorder {

    void saveDepositEvent(DepositCreatedEvent domainEvent);

    void saveWithdrawalEvent(WithdrawalCreatedEvent domainEvent);
}
