package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;

import java.time.ZonedDateTime;

public class UserBalanceUpdatedEvent extends DomainEvent<UserBalance> {

    public UserBalanceUpdatedEvent(UserBalance domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
