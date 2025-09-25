package shop.shportfolio.trading.application.ports.output.kafka;

import shop.shportfolio.common.domain.event.DomainEventPublisher;
import shop.shportfolio.trading.domain.event.DepositWithdrawalUpdatedEvent;
import shop.shportfolio.trading.domain.model.DepositWithdrawal;

public interface TradingDepositWithdrawalPublisher extends DomainEventPublisher<DepositWithdrawalUpdatedEvent> {
}
