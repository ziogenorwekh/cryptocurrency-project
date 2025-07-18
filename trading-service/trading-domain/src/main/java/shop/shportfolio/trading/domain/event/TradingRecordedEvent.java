package shop.shportfolio.trading.domain.event;

import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.domain.entity.trade.Trade;

import java.time.ZonedDateTime;

public class TradingRecordedEvent extends DomainEvent<Trade> {

    public TradingRecordedEvent(Trade domainType, MessageType messageType,
                                ZonedDateTime publishedAt) {
        super(domainType, messageType, publishedAt);
    }
}
