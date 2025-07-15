package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface TradingTrackUseCase {

    OrderBook  findOrderBook(OrderBookTrackQuery orderBookTrackQuery);

    LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query);

    MarketItem findMarketItemByMarketItemId(MarketTrackQuery marketTrackQuery);

    List<MarketItem> findAllMarketItems();

    List<CandleDayResponseDto> findCandleDayByMarket(CandleTrackQuery candleTrackQuery);

    List<CandleWeekResponseDto> findCandleWeekByMarket(CandleTrackQuery candleTrackQuery);

    List<CandleMonthResponseDto> findCandleMonthByMarket(CandleTrackQuery candleTrackQuery);

    List<CandleMinuteResponseDto> findCandleMinuteByMarket(CandleMinuteTrackQuery candleMinuteTrackQuery);

    MarketTickerResponseDto findMarketTickerByMarket(TickerTrackQuery tickerTrackQuery);
}
