package shop.shportfolio.matching.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.matching.domain.entity.MatchingOrderCancel;

import java.time.ZonedDateTime;

public class MatchingOrderCancelDeletedEvent extends DomainEvent<MatchingOrderCancel> {
    public MatchingOrderCancelDeletedEvent(MatchingOrderCancel domainType, MessageType messageType, ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
