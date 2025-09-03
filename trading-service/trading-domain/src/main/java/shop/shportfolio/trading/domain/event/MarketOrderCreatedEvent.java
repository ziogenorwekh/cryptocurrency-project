package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.MarketOrder;

import java.time.ZonedDateTime;

public class MarketOrderCreatedEvent extends DomainEvent<MarketOrder> {
    public MarketOrderCreatedEvent(MarketOrder domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
