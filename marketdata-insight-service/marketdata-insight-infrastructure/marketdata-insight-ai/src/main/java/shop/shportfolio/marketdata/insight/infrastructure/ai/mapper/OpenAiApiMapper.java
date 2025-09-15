package shop.shportfolio.marketdata.insight.infrastructure.ai.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend;
import shop.shportfolio.marketdata.insight.domain.valueobject.Signal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class OpenAiApiMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public OpenAiApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public AiAnalysisResponseDto toAiAnalysisResponseDto(String response) {
        if (response == null || response.isBlank()) return defaultAnalysisResponse();

        try {
            JsonNode root = objectMapper.readTree(response);

            String marketId = root.path("marketId").asText("UNKNOWN");
            OffsetDateTime analysisTime = parseOffsetDateTime(root.get("analysisTime"));
            OffsetDateTime periodStart = parseOffsetDateTime(root.get("periodStart"));
            OffsetDateTime periodEnd = parseOffsetDateTime(root.get("periodEnd"));
            Double momentumScore = root.has("momentumScore") ? root.get("momentumScore").asDouble() : null;
            String priceTrendStr = root.path("priceTrend").asText(null);
            String signalStr = root.path("signal").asText(null);
            String periodTypeStr = root.path("periodType").asText(null);
            String summaryCommentEng = root.path("summaryCommentENG").asText("");
            String summaryCommentKor = root.path("summaryCommentKOR").asText("");

            AiAnalysisResponseDto analysisResponseDto = AiAnalysisResponseDto.builder()
                    .marketId(marketId)
                    .analysisTime(analysisTime)
                    .momentumScore(momentumScore != null ? BigDecimal.valueOf(momentumScore) : null)
                    .priceTrend(mapPriceTrend(priceTrendStr))
                    .signal(mapSignal(signalStr))
                    .periodType(mapPeriodType(periodTypeStr))
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .summaryCommentEng(summaryCommentEng)
                    .summaryCommentKor(summaryCommentKor)
                    .build();
            log.info("AiAnalysisResponseDto: {}", analysisResponseDto);
            return analysisResponseDto;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response, returning default. Error: {}", e.getMessage(), e);
            return defaultAnalysisResponse();
        }
    }

    public AiAnalysisResponseDto aiAnalysisResultToAiAnalysisResult(AIAnalysisResult aiAnalysisResult) {
        return AiAnalysisResponseDto.builder()
                .marketId(aiAnalysisResult.getMarketId().getValue())
                .analysisTime(aiAnalysisResult.getAnalysisTime().getValue())
                .momentumScore(aiAnalysisResult.getMomentumScore().getValue())
                .periodEnd(aiAnalysisResult.getPeriodEnd().getValue())
                .periodStart(aiAnalysisResult.getPeriodStart().getValue())
                .periodType(aiAnalysisResult.getPeriodType())
                .priceTrend(aiAnalysisResult.getPriceTrend())
                .signal(aiAnalysisResult.getSignal())
                .summaryCommentEng(aiAnalysisResult.getSummaryCommentEng().getValue())
                .summaryCommentKor(aiAnalysisResult.getSummaryCommentKor().getValue())
                .build();
    }


    private PriceTrend mapPriceTrend(String aiValue) {
        if (aiValue == null) return PriceTrend.NEUTRAL;
        return switch (aiValue.toUpperCase()) {
            case "UPWARD", "UP" -> PriceTrend.UP;
            case "DOWNWARD", "DOWN" -> PriceTrend.DOWN;
            case "SIDEWAYS" -> PriceTrend.SIDEWAYS;
            default -> PriceTrend.NEUTRAL;
        };
    }

    private Signal mapSignal(String aiValue) {
        if (aiValue == null) return Signal.WAIT;
        return switch (aiValue.toUpperCase()) {
            case "BUY" -> Signal.BUY;
            case "SELL" -> Signal.SELL;
            case "HOLD" -> Signal.HOLD;
            default -> Signal.WAIT;
        };
    }

    private PeriodType mapPeriodType(String aiValue) {
        if (aiValue == null) return PeriodType.THIRTY_MINUTES;
        return switch (aiValue.toUpperCase()) {
            case "THIRTY_MINUTES" -> PeriodType.THIRTY_MINUTES;
            case "ONE_HOUR" -> PeriodType.ONE_HOUR;
            case "DAILY", "ONE_DAY" -> PeriodType.ONE_DAY;
            case "WEEKLY", "ONE_WEEK" -> PeriodType.ONE_WEEK;
            case "ONE_MONTH" -> PeriodType.ONE_MONTH;
            default -> {
                log.warn("Unknown PeriodType from AI: {}", aiValue);
                yield PeriodType.THIRTY_MINUTES;
            }
        };
    }

    private OffsetDateTime parseOffsetDateTime(JsonNode node) {
        if (node == null || node.isNull() || node.asText().isBlank()) return null;
        return OffsetDateTime.parse(node.asText());
    }

    private AiAnalysisResponseDto defaultAnalysisResponse() {
        return AiAnalysisResponseDto.builder()
                .marketId("UNKNOWN")
                .analysisTime(null)
                .momentumScore(null)
                .periodEnd(null)
                .periodStart(null)
                .periodType(PeriodType.THIRTY_MINUTES)
                .priceTrend(PriceTrend.NEUTRAL)
                .signal(Signal.WAIT)
                .summaryCommentEng("")
                .summaryCommentKor("")
                .build();
    }
}
