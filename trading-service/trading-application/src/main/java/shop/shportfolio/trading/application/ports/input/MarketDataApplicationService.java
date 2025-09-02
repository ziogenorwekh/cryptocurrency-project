package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;

import java.util.List;

public interface MarketDataApplicationService {

    /**
     * 마켓 코드로 마켓 정보 조회
     * @param marketTrackQuery
     * @return
     */
    TickerTrackResponse findMarketTicker(@Valid TickerTrackQuery tickerTrackQuery);

    /**
     * 전체 마켓 코드 조회
     * @return
     */
    List<TradeTickResponse> findTradeTick(@Valid TradeTickTrackQuery tradeTickTrackQuery);
}
