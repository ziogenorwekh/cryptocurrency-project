package shop.shportfolio.marketdata.insight.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.request.CandleMinuteTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.CandleTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.MarketTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.*;

import java.util.List;

public interface MarketDataApplicationService {


    /**
     * 마켓 코드로 마켓 정보 조회
     * @param query
     * @return
     */
    MarketCodeTrackResponse findMarketByMarketId(@Valid MarketTrackQuery query);

    /**
     * 전체 마켓 코드 조회
     * @return
     */
    List<MarketCodeTrackResponse> findAllMarkets();

    /**
     * 분봉 캔들 조회
     * @param candleMinuteTrackQuery
     * @return
     */
    List<CandleMinuteTrackResponse> findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery);

    /**
     * 일봉 캔들 조회
     * @param candleTrackQuery
     * @return
     */
    List<CandleDayTrackResponse> findCandleDay(@Valid CandleTrackQuery candleTrackQuery);

    /**
     * 주봉 캔들 조회
     * @param candleTrackQuery
     * @return
     */
    List<CandleWeekTrackResponse> findCandleWeek(@Valid CandleTrackQuery candleTrackQuery);

    /**
     * 월봉 캔들 조회
     * @param candleTrackQuery
     * @return
     */
    List<CandleMonthTrackResponse> findCandleMonth(@Valid CandleTrackQuery candleTrackQuery);

}
