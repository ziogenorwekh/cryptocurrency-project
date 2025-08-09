package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.CandleMinuteTrackQuery;
import shop.shportfolio.trading.application.command.track.request.CandleTrackQuery;
import shop.shportfolio.trading.application.command.track.response.CandleDayTrackResponse;
import shop.shportfolio.trading.application.command.track.response.CandleMinuteTrackResponse;
import shop.shportfolio.trading.application.command.track.response.CandleMonthTrackResponse;
import shop.shportfolio.trading.application.command.track.response.CandleWeekTrackResponse;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CandleResources {

    private final MarketDataApplicationService marketDataApplicationService;

    @Autowired
    public CandleResources(MarketDataApplicationService marketDataApplicationService) {
        this.marketDataApplicationService = marketDataApplicationService;
    }

    @RequestMapping(path = "/track/candle/minute/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<CandleMinuteTrackResponse>> findCandleMinute(
            @RequestBody CandleMinuteTrackQuery candleMinuteTrackQuery
            ,@PathVariable String marketId) {
        candleMinuteTrackQuery.setMarket(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findCandleMinute(candleMinuteTrackQuery));
    }

    @RequestMapping(path = "/track/candle/day/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<CandleDayTrackResponse>> findCandleDay(
            @PathVariable String marketId,
            @RequestBody CandleTrackQuery candleTrackQuery) {
        candleTrackQuery.setMarketId(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findCandleDay(candleTrackQuery));
    }

    @RequestMapping(path = "/track/candle/week/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<CandleWeekTrackResponse>> findCandleWeek(
            @PathVariable String marketId,
            @RequestBody CandleTrackQuery candleTrackQuery) {
        candleTrackQuery.setMarketId(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findCandleWeek(candleTrackQuery));
    }

    @RequestMapping(path = "/track/candle/month/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<CandleMonthTrackResponse>> findCandleMonth(
            @PathVariable String marketId,
            @RequestBody CandleTrackQuery candleTrackQuery) {
        candleTrackQuery.setMarketId(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findCandleMonth(candleTrackQuery));
    }


}
