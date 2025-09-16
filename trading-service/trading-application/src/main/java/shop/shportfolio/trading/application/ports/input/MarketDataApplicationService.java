package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;

import java.util.List;

public interface MarketDataApplicationService {

    TickerTrackResponse findMarketTicker(@Valid TickerTrackQuery tickerTrackQuery);

    List<TickerTrackResponse> findAllMarketTicker();

    List<TradeTickTrackResponse> findTradeTick(@Valid TradeTickTrackQuery tradeTickTrackQuery);
}
