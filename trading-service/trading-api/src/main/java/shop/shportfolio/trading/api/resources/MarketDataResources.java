package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.CandleMinuteTrackQuery;
import shop.shportfolio.trading.application.command.track.request.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.response.CandleMinuteTrackResponse;
import shop.shportfolio.trading.application.command.track.response.MarketCodeTrackResponse;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MarketDataResources {

    private final MarketDataApplicationService tradingApplicationService;

    @Autowired
    public MarketDataResources(MarketDataApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @RequestMapping(path = "/track/marketdata/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<MarketCodeTrackResponse> findMarketById(@PathVariable String marketId) {
        MarketTrackQuery marketTrackQuery = new MarketTrackQuery(marketId);
        return ResponseEntity.ok(tradingApplicationService.findMarketById(marketTrackQuery));
    }

    @RequestMapping(path = "/track/marketdata/all",method = RequestMethod.GET)
    public ResponseEntity<List<MarketCodeTrackResponse>> findAllMarkets() {
        return ResponseEntity.ok(tradingApplicationService.findAllMarkets());
    }

    @RequestMapping(path = "/track/candle/minute/{marketId}")
    public ResponseEntity<List<CandleMinuteTrackResponse>>  findCandleMinute(
            @RequestBody CandleMinuteTrackQuery candleMinuteTrackQuery
    ,@PathVariable String marketId) {
        candleMinuteTrackQuery.setMarket(marketId);
        return ResponseEntity.ok(tradingApplicationService.findCandleMinute(candleMinuteTrackQuery));
    }



}
