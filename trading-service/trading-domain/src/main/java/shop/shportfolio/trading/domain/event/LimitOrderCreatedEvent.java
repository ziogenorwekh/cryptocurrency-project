package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.LimitOrder;

import java.time.ZonedDateTime;

public class LimitOrderCreatedEvent extends DomainEvent<LimitOrder> {
    public LimitOrderCreatedEvent(LimitOrder domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
