package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface TradingTrackUseCase {

//    OrderBook  findOrderBook(OrderBookTrackQuery orderBookTrackQuery);

    LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query);

    MarketTickerResponseDto findMarketTickerByMarket(TickerTrackQuery tickerTrackQuery);

    List<MarketTickerResponseDto> findAllMarketTicker();

    List<TradeTickResponseDto> findTradeTickByMarket(TradeTickTrackQuery trackQuery);

    List<Order> findAllOrderByMarketId(OrderTrackQuery orderTrackQuery);
}
