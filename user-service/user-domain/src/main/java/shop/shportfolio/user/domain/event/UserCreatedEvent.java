package shop.shportfolio.user.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.User;

import java.time.ZonedDateTime;

public class UserCreatedEvent extends DomainEvent<User> {
    public UserCreatedEvent(User domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
