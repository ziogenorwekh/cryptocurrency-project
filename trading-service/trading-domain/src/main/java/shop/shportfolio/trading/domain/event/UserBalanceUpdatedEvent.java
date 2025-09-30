package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.view.UserBalanceView;

import java.time.ZonedDateTime;

public class UserBalanceUpdatedEvent extends DomainEvent<UserBalanceView> {

    public UserBalanceUpdatedEvent(UserBalanceView domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
