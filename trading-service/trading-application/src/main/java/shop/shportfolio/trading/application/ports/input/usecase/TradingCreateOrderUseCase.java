package shop.shportfolio.trading.application.ports.input.usecase;

import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateReservationOrderCommand;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;

public interface TradingCreateOrderUseCase {


    LimitOrderCreatedEvent createLimitOrder(CreateLimitOrderCommand command);

    MarketOrderCreatedEvent createMarketOrder(CreateMarketOrderCommand command);

    ReservationOrderCreatedEvent createReservationOrder(CreateReservationOrderCommand command);
}
