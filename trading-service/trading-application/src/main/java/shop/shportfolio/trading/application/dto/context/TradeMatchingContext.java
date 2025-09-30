package shop.shportfolio.trading.application.dto.context;

import lombok.Getter;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class TradeMatchingContext {

    private final Optional<TradeCreatedEvent> tradingRecordedEvents;
    private final Optional<UserBalanceUpdatedEvent> userBalanceUpdatedEvent;

    public TradeMatchingContext(Optional<TradeCreatedEvent> tradingRecordedEvents,
                                Optional<UserBalanceUpdatedEvent> userBalanceUpdatedEvent) {
        this.tradingRecordedEvents = tradingRecordedEvents;
        this.userBalanceUpdatedEvent = userBalanceUpdatedEvent;
    }
}
