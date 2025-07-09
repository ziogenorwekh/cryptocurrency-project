package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.domain.entity.Order;

public interface OrderValidator<T extends Order> {

    boolean validateBuyOrder(T order);

    boolean validateSellOrder(T order);

}
