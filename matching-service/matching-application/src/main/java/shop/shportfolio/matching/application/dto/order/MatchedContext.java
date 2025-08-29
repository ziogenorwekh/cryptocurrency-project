package shop.shportfolio.matching.application.dto.order;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.util.List;

@Getter
public class MatchedContext<T extends Order> {

    private final List<PredictedTradeCreatedEvent> tradeCreatedEvents;
    private final T order;

    @Builder
    public MatchedContext(List<PredictedTradeCreatedEvent> tradeCreatedEvents, T order) {
        this.tradeCreatedEvents = tradeCreatedEvents;
        this.order = order;
    }
}
