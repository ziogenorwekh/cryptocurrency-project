package shop.shportfolio.marketdata.insight.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.marketdata.insight.application.command.request.CandleMinuteTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.CandleTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.MarketTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.*;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.MarketDataApplicationService;

import java.util.List;

@Slf4j
@RequestMapping("/api")
@RestController
@Tag(name = "Market Data API", description = "시장 캔들 데이터 관련 API")
public class MarketDataResources {


    private final MarketDataApplicationService marketDataApplicationService;

    @Autowired
    public MarketDataResources(MarketDataApplicationService marketDataApplicationService) {
        this.marketDataApplicationService = marketDataApplicationService;
    }

    @Operation(
            summary = "시장 코드 추적",
            description = "특정 시장 ID에 대한 시장 코드를 추적합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 시장 코드를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "요청한 시장 ID에 해당하는 시장을 찾을 수 없습니다.")
            }
    )
    @RequestMapping(path = "/markets/{marketId}/track", produces = "application/json",method = RequestMethod.GET)
    public ResponseEntity<MarketCodeTrackResponse> trackMarketCodes(@PathVariable("marketId") String marketId) {
        MarketTrackQuery query = new MarketTrackQuery(marketId);
        MarketCodeTrackResponse response = marketDataApplicationService.findMarketByMarketId(query);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "모든 시장 조회",
            description = "등록된 모든 시장 코드를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 모든 시장 코드를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요.")
            }
    )
    @RequestMapping(path = "/markets", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<MarketCodeTrackResponse>> findAllMarkets() {
        List<MarketCodeTrackResponse> response = marketDataApplicationService.findAllMarkets();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "분 단위 캔들 데이터 추적",
            description = "특정 시장 ID와 분 단위(예: 1분, 3분, 5분 등)에 대한 캔들 데이터를 추적합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 캔들 데이터를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "요청한 시장 ID에 해당하는 시장을 찾을 수 없습니다.")
            }
    )
    @RequestMapping(path = "/candles/{marketId}/minutes/{unit}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<CandleMinuteTrackResponse>> trackCandleMinutes(
            @PathVariable("marketId") String marketId,
            @PathVariable("unit") int unit,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer count) {
        CandleMinuteTrackQuery candleMinuteTrackQuery = new CandleMinuteTrackQuery();
        candleMinuteTrackQuery.setMarket(marketId);
        candleMinuteTrackQuery.setUnit(unit);
        candleMinuteTrackQuery.setTo(to);
        candleMinuteTrackQuery.setCount(count);
        List<CandleMinuteTrackResponse> response = marketDataApplicationService.findCandleMinute(candleMinuteTrackQuery);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "일 단위 캔들 데이터 추적",
            description = "특정 시장 ID에 대한 일 단위 캔들 데이터를 추적합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 일 단위 캔들 데이터를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "요청한 시장 ID에 해당하는 시장을 찾을 수 없습니다.")
            }
    )
    @RequestMapping(path = "/candles/{marketId}/days", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<CandleDayTrackResponse>> trackCandleDays(
            @PathVariable("marketId") String marketId,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer count) {

        CandleTrackQuery query = new CandleTrackQuery();
        query.setMarketId(marketId);
        query.setTo(to);
        query.setCount(count);

        List<CandleDayTrackResponse> response = marketDataApplicationService.findCandleDay(query);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "주 단위 캔들 데이터 추적",
            description = "특정 시장 ID에 대한 주 단위 캔들 데이터를 추적합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 주 단위 캔들 데이터를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "요청한 시장 ID에 해당하는 시장을 찾을 수 없습니다.")
            }
    )
    @RequestMapping(path = "/candles/{marketId}/weeks", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<CandleWeekTrackResponse>> trackCandleWeeks(
            @PathVariable("marketId") String marketId,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer count) {

        CandleTrackQuery query = new CandleTrackQuery();
        query.setMarketId(marketId);
        query.setTo(to);
        query.setCount(count);

        List<CandleWeekTrackResponse> response = marketDataApplicationService.findCandleWeek(query);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "월 단위 캔들 데이터 추적",
            description = "특정 시장 ID에 대한 월 단위 캔들 데이터를 추적합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "성공적으로 월 단위 캔들 데이터를 반환했습니다."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "요청한 시장 ID에 해당하는 시장을 찾을 수 없습니다.")
            }
    )
    @RequestMapping(path = "/candles/{marketId}/months", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<CandleMonthTrackResponse>> trackCandleMonths(
            @PathVariable("marketId") String marketId,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) Integer count) {

        CandleTrackQuery query = new CandleTrackQuery();
        query.setMarketId(marketId);
        query.setTo(to);
        query.setCount(count);

        List<CandleMonthTrackResponse> response = marketDataApplicationService.findCandleMonth(query);
        return ResponseEntity.ok(response);
    }

}
