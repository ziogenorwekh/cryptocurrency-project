package shop.shportfolio.matching.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.matching.domain.entity.PredictedTrade;

import java.time.ZonedDateTime;

public class PredictedTradeCreatedEvent extends DomainEvent<PredictedTrade> {

    public PredictedTradeCreatedEvent(PredictedTrade domainType,
                                      MessageType messageType,
                                      ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
