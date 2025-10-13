package shop.shportfolio.marketdata.insight.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.InsightApplicationService;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Insight API", description = "시장 데이터 AI 분석 관련 API")
public class InsightResources {


    private final InsightApplicationService insightApplicationService;

    @Autowired
    public InsightResources(InsightApplicationService insightApplicationService) {
        this.insightApplicationService = insightApplicationService;
    }


    @Operation(
            summary = "AI 분석 추적",
            description = "특정 시장과 기간에 대한 AI 분석 결과를 추적합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공적으로 AI 분석 결과를 반환했습니다."),
                    @ApiResponse(responseCode = "400",
                            description = "잘못된 요청입니다. 요청 파라미터를 확인하세요."),
                    @ApiResponse(responseCode = "503",
                            description = "AI 서비스가 일시적으로 사용 불가능합니다. 나중에 다시 시도하세요.")
            }
    )
    @RequestMapping(path = "/insights/ai-analysis/track/{marketId}",
            produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<AiAnalysisTrackResponse> trackAiAnalysis(
            @RequestParam PeriodType periodType,
            @PathVariable String marketId) {
        AiAnalysisTrackQuery aiAnalysisTrackQuery = new AiAnalysisTrackQuery(marketId, periodType);
        AiAnalysisTrackResponse response = insightApplicationService.trackAiAnalysis(aiAnalysisTrackQuery);
        log.info("response marketId : {}", response.getMarketId());
        return ResponseEntity.ok(response);
    }
}
