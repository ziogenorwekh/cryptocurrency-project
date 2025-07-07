package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface TradingUpdateUseCase {

    LimitOrder cancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand);

    ReservationOrder cancelReservationOrder(CancelReservationOrderCommand cancelReservationOrderCommand);
}
