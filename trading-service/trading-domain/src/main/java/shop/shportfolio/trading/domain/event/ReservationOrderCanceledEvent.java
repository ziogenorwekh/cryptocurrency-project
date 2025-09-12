package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.time.ZonedDateTime;

public class ReservationOrderCanceledEvent extends DomainEvent<ReservationOrder> {
    public ReservationOrderCanceledEvent(ReservationOrder domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
