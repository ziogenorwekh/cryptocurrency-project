package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface ReservationOrderCancelListener {

    void cancelReservationOrder(CancelOrderDto cancelOrderDto);

    void revertReservationOrder(CancelOrderDto cancelOrderDto);
}
