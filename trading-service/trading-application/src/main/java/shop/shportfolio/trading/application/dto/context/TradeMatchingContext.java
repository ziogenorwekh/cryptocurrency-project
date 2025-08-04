package shop.shportfolio.trading.application.dto.context;

import lombok.Getter;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TradeMatchingContext {

    private final List<TradeCreatedEvent> tradingRecordedEvents;
    private final UserBalanceUpdatedEvent userBalanceUpdatedEvent;

    public TradeMatchingContext(List<TradeCreatedEvent> tradingRecordedEvents,
                                UserBalanceUpdatedEvent userBalanceUpdatedEvent) {
        this.tradingRecordedEvents = tradingRecordedEvents;
        this.userBalanceUpdatedEvent = userBalanceUpdatedEvent;
    }
}
