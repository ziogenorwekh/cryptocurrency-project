package shop.shportfolio.trading.application.dto.context;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.Order;

@Getter
public class OrderCreationContext<T extends DomainEvent> {
    private final T domainEvent;
    private final MarketItem marketItem;

    @Builder
    public OrderCreationContext(T domainEvent, MarketItem marketItem) {
        this.domainEvent = domainEvent;
        this.marketItem = marketItem;
    }
}
