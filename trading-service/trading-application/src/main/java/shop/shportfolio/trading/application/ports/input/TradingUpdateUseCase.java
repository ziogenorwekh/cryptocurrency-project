package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;

public interface TradingUpdateUseCase {

    LimitOrderCanceledEvent pendingCancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand);

    ReservationOrderCanceledEvent pendingCancelReservationOrder(CancelReservationOrderCommand cancelReservationOrderCommand);
}
