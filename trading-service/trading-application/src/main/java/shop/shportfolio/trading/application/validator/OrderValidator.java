package shop.shportfolio.trading.application.validator;

import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.Order;

public interface OrderValidator<T extends Order> {

    boolean supports(Order order);

    void validateBuyOrder(T order, MarketItem marketItem);

    void validateSellOrder(T order,MarketItem marketItem);

}
