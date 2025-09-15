package shop.shportfolio.portfolio.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.portfolio.domain.entity.view.CryptoView;

import java.time.ZonedDateTime;

public class CryptoUpdatedEvent extends DomainEvent<CryptoView> {
    public CryptoUpdatedEvent(CryptoView domainType, MessageType messageType,
                              ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
