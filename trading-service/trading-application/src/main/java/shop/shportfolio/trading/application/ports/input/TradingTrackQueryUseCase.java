package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.domain.entity.OrderBook;

public interface TradingTrackQueryUseCase {

    OrderBook  findOrderBook(OrderBookTrackQuery orderBookTrackQuery);
}
