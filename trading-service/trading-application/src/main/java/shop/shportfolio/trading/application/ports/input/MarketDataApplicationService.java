package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.track.*;
import shop.shportfolio.trading.application.dto.marketdata.CandleTrackQuery;

import java.util.List;

public interface MarketDataApplicationService {

    MarketCodeTrackResponse findMarketById(@Valid MarketTrackQuery marketTrackQuery);

    List<MarketCodeTrackResponse> findAllMarkets();

    CandleMinuteTrackResponse findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery);

    CandleDayTrackResponse findCandleDay(@Valid CandleTrackQuery candleTrackQuery);

    CandleWeekTrackResponse findCandleWeek(@Valid CandleTrackQuery candleTrackQuery);

    CandleMonthTrackResponse findCandleMonth(@Valid CandleTrackQuery candleTrackQuery);

}
