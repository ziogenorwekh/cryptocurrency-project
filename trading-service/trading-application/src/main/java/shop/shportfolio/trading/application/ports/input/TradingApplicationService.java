package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;

public interface TradingApplicationService {

    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);

    void createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand);

    OrderBookTrackResponse findOrderBook(@Valid OrderBookTrackQuery orderBookTrackQuery);

    LimitOrderTrackResponse  findLimitOrderTrackByOrderId(@Valid LimitOrderTrackQuery limitOrderTrackQuery);
}
