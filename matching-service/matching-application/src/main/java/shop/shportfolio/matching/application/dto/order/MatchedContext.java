package shop.shportfolio.matching.application.dto.order;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.util.List;

@Getter
public class MatchedContext<T extends Order> {

    private final List<TradeCreatedEvent> tradeCreatedEvents;
    private final T order;

    @Builder
    public MatchedContext(List<TradeCreatedEvent> tradeCreatedEvents, T order) {
        this.tradeCreatedEvents = tradeCreatedEvents;
        this.order = order;
    }
}
