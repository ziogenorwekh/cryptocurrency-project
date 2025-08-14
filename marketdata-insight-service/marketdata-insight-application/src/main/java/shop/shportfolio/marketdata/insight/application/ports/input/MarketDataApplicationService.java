package shop.shportfolio.marketdata.insight.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.*;
import shop.shportfolio.marketdata.insight.application.command.response.*;

import java.util.List;

public interface MarketDataApplicationService {


    MarketCodeTrackResponse findMarketByCode(@Valid MarketTrackQuery query);

    List<MarketCodeTrackResponse> findAllMarkets();

    List<CandleMinuteTrackResponse> findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery);

    List<CandleDayTrackResponse> findCandleDay(@Valid CandleTrackQuery candleTrackQuery);

    List<CandleWeekTrackResponse> findCandleWeek(@Valid CandleTrackQuery candleTrackQuery);

    List<CandleMonthTrackResponse> findCandleMonth(@Valid CandleTrackQuery candleTrackQuery);

}
