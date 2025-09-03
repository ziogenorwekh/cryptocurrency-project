package shop.shportfolio.user.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.UserId;

import java.time.ZonedDateTime;

public class UserCreatedEvent extends DomainEvent<UserId> {
    public UserCreatedEvent(UserId domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
