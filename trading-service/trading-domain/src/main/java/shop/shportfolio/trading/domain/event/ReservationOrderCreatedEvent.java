package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.time.ZonedDateTime;

public class ReservationOrderCreatedEvent extends DomainEvent<ReservationOrder> {
    public ReservationOrderCreatedEvent(ReservationOrder domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
