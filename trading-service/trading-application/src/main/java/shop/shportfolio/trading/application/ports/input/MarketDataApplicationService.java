package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;

import java.util.List;

public interface MarketDataApplicationService {

    MarketCodeTrackResponse findMarketById(@Valid MarketTrackQuery marketTrackQuery);

    List<MarketCodeTrackResponse> findAllMarkets();

    List<CandleMinuteTrackResponse> findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery);

    List<CandleDayTrackResponse> findCandleDay(@Valid CandleTrackQuery candleTrackQuery);

    List<CandleWeekTrackResponse> findCandleWeek(@Valid CandleTrackQuery candleTrackQuery);

    List<CandleMonthTrackResponse> findCandleMonth(@Valid CandleTrackQuery candleTrackQuery);

    TickerTrackResponse findMarketTicker(@Valid TickerTrackQuery tickerTrackQuery);

    List<TradeTickResponse> findTradeTick(@Valid TradeTickTrackQuery tradeTickTrackQuery);
}
