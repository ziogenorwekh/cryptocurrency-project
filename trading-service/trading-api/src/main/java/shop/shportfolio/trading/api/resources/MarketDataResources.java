package shop.shportfolio.trading.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.TickerTrackQuery;
import shop.shportfolio.trading.application.command.track.request.TradeTickTrackQuery;
import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;
import shop.shportfolio.trading.application.command.track.response.TradeTickTrackResponse;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Market Data API", description = "시장 데이터 추적 관련 API")
public class MarketDataResources {

    private final MarketDataApplicationService marketDataApplicationService;

    @Autowired
    public MarketDataResources(MarketDataApplicationService marketDataApplicationService) {
        this.marketDataApplicationService = marketDataApplicationService;
    }


    @Operation(
            summary = "마켓 티커 조회",
            description = "특정 마켓의 현재 티커 데이터를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 티커 데이터를 반환했습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
            }
    )
    @RequestMapping(path = "/track/marketdata/ticker/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<TickerTrackResponse> findMarketTicker(@PathVariable String marketId) {
        TickerTrackQuery tickerTrackQuery = new TickerTrackQuery(marketId);
        return ResponseEntity.ok(marketDataApplicationService.findMarketTicker(tickerTrackQuery));
    }

    @Operation(
            summary = "마켓 체결 내역 조회",
            description = "특정 마켓의 체결 데이터를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 체결 데이터를 반환했습니다."),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
            }
    )
    @RequestMapping(path = "/track/marketdata/trade/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<List<TradeTickTrackResponse>> findTradeTick(@PathVariable String marketId,
                                                                      @RequestParam(required = false) String to,
                                                                      @RequestParam(required = false) Integer count,
                                                                      @RequestParam(required = false) String cursor,
                                                                      @RequestParam(required = false) Integer daysAgo
                                                                      ) {
        TradeTickTrackQuery query = new TradeTickTrackQuery();
        query.setMarketId(marketId);
        query.setTo(to);
        query.setCount(count);
        query.setCursor(cursor);
        query.setDaysAgo(daysAgo);
        return ResponseEntity.ok(marketDataApplicationService.findTradeTick(query));
    }


}
