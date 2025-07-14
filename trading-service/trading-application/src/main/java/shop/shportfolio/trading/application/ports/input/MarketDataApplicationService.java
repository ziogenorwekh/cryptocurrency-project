package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.request.CandleMinuteTrackQuery;
import shop.shportfolio.trading.application.command.track.request.CandleTrackQuery;
import shop.shportfolio.trading.application.command.track.request.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.response.*;

import java.util.List;

public interface MarketDataApplicationService {

    MarketCodeTrackResponse findMarketById(MarketTrackQuery marketTrackQuery);

    List<MarketCodeTrackResponse> findAllMarkets();

    CandleMinuteTrackResponse findCandleMinute(CandleMinuteTrackQuery candleMinuteTrackQuery);

    CandleDayTrackResponse findCandleDay(CandleTrackQuery candleTrackQuery);

    CandleWeekTrackResponse findCandleWeek(CandleTrackQuery candleTrackQuery);

    CandleMonthTrackResponse findCandleMonth(CandleTrackQuery candleTrackQuery);

}
