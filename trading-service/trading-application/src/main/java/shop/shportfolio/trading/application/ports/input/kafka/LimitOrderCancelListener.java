package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.order.CancelOrderDto;

public interface LimitOrderCancelListener {

    void cancelLimitOrder(CancelOrderDto cancelOrderDto);

    void revertLimitOrder(CancelOrderDto cancelOrderDto);
}
