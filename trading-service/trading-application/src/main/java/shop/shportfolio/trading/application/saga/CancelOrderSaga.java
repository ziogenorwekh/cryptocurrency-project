package shop.shportfolio.trading.application.saga;

import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;

public interface CancelOrderSaga {

    LimitOrderCanceledEvent pendingCancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand);

    ReservationOrderCanceledEvent pendingCancelReservationOrder(CancelReservationOrderCommand cancelReservationOrderCommand);

    @Transactional
    void cancelLimitOrder(CancelOrderDto cancelOrderDto);

    @Transactional
    void revertLimitOrder(CancelOrderDto cancelOrderDto);

    @Transactional
    void cancelReservationOrder(CancelOrderDto cancelOrderDto);

    @Transactional
    void revertReservationOrder(CancelOrderDto cancelOrderDto);
}
