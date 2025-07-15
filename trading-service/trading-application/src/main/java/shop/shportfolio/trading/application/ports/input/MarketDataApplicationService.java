package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;

import java.util.List;

public interface MarketDataApplicationService {

    MarketCodeTrackResponse findMarketById(MarketTrackQuery marketTrackQuery);

    List<MarketCodeTrackResponse> findAllMarkets();

    List<CandleMinuteTrackResponse> findCandleMinute(CandleMinuteTrackQuery candleMinuteTrackQuery);

    List<CandleDayTrackResponse> findCandleDay(CandleTrackQuery candleTrackQuery);

    List<CandleWeekTrackResponse> findCandleWeek(CandleTrackQuery candleTrackQuery);

    List<CandleMonthTrackResponse> findCandleMonth(CandleTrackQuery candleTrackQuery);

    TickerTrackResponse findMarketTicker(TickerTrackQuery tickerTrackQuery);

    List<TradeTickResponse> findTradeTick(TradeTickTrackQuery tradeTickTrackQuery);
}
