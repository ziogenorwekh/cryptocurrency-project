package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface TradingUpdateUseCase {

    LimitOrder pendingCancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand);

    ReservationOrder pendingCancelReservationOrder(CancelReservationOrderCommand cancelReservationOrderCommand);
}
