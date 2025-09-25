package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.model.DepositWithdrawal;

import java.time.ZonedDateTime;

public class DepositWithdrawalUpdatedEvent extends DomainEvent<DepositWithdrawal> {

    public DepositWithdrawalUpdatedEvent(DepositWithdrawal domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
