package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.request.TickerTrackQuery;
import shop.shportfolio.trading.application.command.track.request.TradeTickTrackQuery;
import shop.shportfolio.trading.application.command.track.response.MarketCodeTrackResponse;
import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;
import shop.shportfolio.trading.application.command.track.response.TradeTickResponse;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MarketDataResources {

    private final MarketDataApplicationService marketDataApplicationService;

    @Autowired
    public MarketDataResources(MarketDataApplicationService marketDataApplicationService) {
        this.marketDataApplicationService = marketDataApplicationService;
    }
//
//    @RequestMapping(path = "/track/marketdata/{marketId}",method = RequestMethod.GET)
//    public ResponseEntity<MarketCodeTrackResponse> findMarketById(@PathVariable String marketId) {
//        MarketTrackQuery marketTrackQuery = new MarketTrackQuery(marketId);
//        return ResponseEntity.ok(marketDataApplicationService.findMarketById(marketTrackQuery));
//    }
//
//    @RequestMapping(path = "/track/marketdata/all",method = RequestMethod.GET)
//    public ResponseEntity<List<MarketCodeTrackResponse>> findAllMarkets() {
//        return ResponseEntity.ok(marketDataApplicationService.findAllMarkets());
//    }

    @RequestMapping(path = "/track/marketdata/ticker/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<TickerTrackResponse> findMarketTicker(@PathVariable String marketId) {
        TickerTrackQuery tickerTrackQuery = new TickerTrackQuery(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findMarketTicker(tickerTrackQuery));
    }

    @RequestMapping(path = "/track/marketdata/trade/ticker/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<TradeTickResponse>> findTradeTick(@PathVariable String marketId,
                                                                 @RequestBody TradeTickTrackQuery query) {
        query.setMarketId(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findTradeTick(query));
    }


}
