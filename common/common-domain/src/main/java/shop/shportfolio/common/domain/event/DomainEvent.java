package shop.shportfolio.common.domain.event;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MessageType;

import java.time.ZonedDateTime;

@Getter
public abstract class DomainEvent<D> {

    private final D domainType;

    private final MessageType messageType;
    private final ZonedDateTime publishedAt;

    protected DomainEvent(D domainType, MessageType messageType, ZonedDateTime publishedAt) {
        this.domainType = domainType;
        this.messageType = messageType;
        this.publishedAt = publishedAt;
    }
}
