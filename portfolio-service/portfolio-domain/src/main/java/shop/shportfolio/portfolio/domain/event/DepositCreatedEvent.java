package shop.shportfolio.portfolio.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;

import java.time.ZonedDateTime;

public class DepositCreatedEvent extends DomainEvent<DepositWithdrawal> {
    public DepositCreatedEvent(DepositWithdrawal domainType,
                               MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
