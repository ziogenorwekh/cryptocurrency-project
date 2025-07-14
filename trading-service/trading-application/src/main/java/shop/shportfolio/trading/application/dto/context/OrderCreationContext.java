package shop.shportfolio.trading.application.dto.context;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.Order;

@Getter
public class OrderCreationContext<T extends Order> {
    private final T order;
    private final MarketItem marketItem;

    @Builder
    public OrderCreationContext(T order, MarketItem marketItem) {
        this.order = order;
        this.marketItem = marketItem;
    }
}
