package shop.shportfolio.matching.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shop.shportfolio.matching.application.command.OrderBookTrackQuery;
import shop.shportfolio.matching.application.command.OrderBookTrackResponse;
import shop.shportfolio.matching.application.ports.input.MatchingApplicationService;

@Tag(name = "Matching API", description = "매칭 관련 API")
@RestController
@RequestMapping("/api")
public class MatchingResources {

    private final MatchingApplicationService matchingApplicationService;

    @Autowired
    public MatchingResources(MatchingApplicationService matchingApplicationService) {
        this.matchingApplicationService = matchingApplicationService;
    }

    /**
     * 특정 마켓 ID에 대한 오더북을 추적하는 API 엔드포인트
     *
     * @param marketId 추적할 마켓 ID
     * @return 오더북 추적 응답과 HTTP 200 상태코드 반환
     */
    @Operation(
            summary = "호가창 추적",
            description = "특정 마켓 ID의 호가창을 추적합니다.",
            tags = {"호가창"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.
                            ApiResponse(responseCode = "200", description = "호가창 조회 성공"
                            , content = @Content(schema = @Schema(implementation = OrderBookTrackResponse.class)))
            }
    )
    @RequestMapping(value = "/matching/orderbook/{marketId}",method = RequestMethod.GET)
    public ResponseEntity<OrderBookTrackResponse> trackOrderBook(@PathVariable("marketId") String marketId) {
        OrderBookTrackResponse response = matchingApplicationService.trackOrderBook(
                new OrderBookTrackQuery(marketId)
        );
        return ResponseEntity.ok(response);
    }
}
