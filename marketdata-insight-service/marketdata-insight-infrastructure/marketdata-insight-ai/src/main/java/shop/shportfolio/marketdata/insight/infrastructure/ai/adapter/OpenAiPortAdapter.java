package shop.shportfolio.marketdata.insight.infrastructure.ai.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.*;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.infrastructure.ai.config.ChatClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.ai.mapper.OpenAiApiMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OpenAiPortAdapter implements OpenAiPort {

    private final ChatClient chatClient;
    private final ChatClientConfigData chatClientConfigData;
    private final OpenAiApiMapper openAiApiMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenAiPortAdapter(ChatClient chatClient, ChatClientConfigData chatClientConfigData,
                             OpenAiApiMapper openAiApiMapper, ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.chatClientConfigData = chatClientConfigData;
        this.openAiApiMapper = openAiApiMapper;
        this.objectMapper = objectMapper.copy();
    }

    @Override
    public AiAnalysisResponseDto analyzeThirtyMinutes(String marketId, List<CandleMinuteResponseDto> dtoList) {
        return safeAnalyze(marketId, dtoList, "THIRTY_MINUTES");

    }

    @Override
    public AiAnalysisResponseDto incrementAnalysisThirtyMinutesWithLatestResult(String marketId,
                                                                                List<CandleMinuteResponseDto> dtos,
                                                                                AIAnalysisResult result) {
        return analyzeWithLatestResult(marketId, dtos, result, "THIRTY_MINUTES");
    }
    @Override
    public AiAnalysisResponseDto analyzeOneHours(String marketId, List<CandleMinuteResponseDto> dtoList) {
        return safeAnalyze(marketId, dtoList, "ONE_HOUR");
    }


    @Override
    public AiAnalysisResponseDto analyzeDays(String marketId, List<CandleDayResponseDto> dtoList) {
        return safeAnalyze(marketId, dtoList, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto incrementAnalysisDaysWithLatestResult(String marketId,
                                                                       List<CandleDayResponseDto> dtos,
                                                                       AIAnalysisResult result) {
        return analyzeWithLatestResult(marketId, dtos, result, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeks(String marketId, List<CandleWeekResponseDto> dtoList) {
        return safeAnalyze(marketId, dtoList, "WEEKLY");
    }
    @Override
    public AiAnalysisResponseDto analyzeOneMonths(String marketId, List<CandleMonthResponseDto> dtoList) {
        return safeAnalyze(marketId, dtoList, "ONE_MONTH");
    }
    // -------------------------
    // 안전한 재시도 + 로그 + 세션 분리
    // -------------------------
    private AiAnalysisResponseDto safeAnalyze(String marketId, List<?> dtoList, String periodType) {
        for (int attempt = 1; attempt <= chatClientConfigData.getMaxTries(); attempt++) {
            try {
                AiAnalysisResponseDto dto = analyze(marketId, dtoList, periodType);
                if (dto != null) {
                    log.info("[AI] {} analysis successful on attempt {}", periodType, attempt);
                    return dto;
                }
            } catch (Exception e) {
                log.error("[AI] {} analysis failed on attempt {}: {}", periodType, attempt, e.getMessage(), e);
            }
        }
        log.warn("[AI] {} analysis failed after {} attempts, returning empty DTO", periodType, chatClientConfigData.getMaxTries());
        return new AiAnalysisResponseDto();
    }

    private AiAnalysisResponseDto analyze(String marketId, List<?> dtoList, String periodType) {
        List<Message> messages = createFirstAnalysisMessages(marketId, dtoList, periodType);

        Prompt prompt = Prompt.builder()
                .messages(new ArrayList<>(messages))
                .build();

        log.info("[AI] Sending {} candle analysis request for market {} with {} messages", periodType, marketId, messages.size());

        String response = chatClient.prompt(prompt)
                .options(ChatOptions.builder().build())
                .call()
                .content();

        log.info("[AI] Response received for {} market {}: {}", periodType, marketId, response);

        if (response == null || response.isBlank()) {
            log.warn("[AI] Empty response for {} market {}", periodType, marketId);
            return null;
        }

        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }

    private AiAnalysisResponseDto analyzeWithLatestResult(String marketId, Object candleDto,
                                                          AIAnalysisResult lastResult, String periodType) {
        List<Message> messages = new ArrayList<>();

        AiAnalysisResponseDto previousDto = openAiApiMapper.aiAnalysisResultToAiAnalysisResult(lastResult);
        String previousJson;
        String candleJson;
        try {
            previousJson = objectMapper.writeValueAsString(previousDto);
            candleJson = objectMapper.writeValueAsString(candleDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DTO to JSON", e);
        }

        messages.add(new UserMessage("Analyze latest candles incrementally for market "
                + marketId + " based on previous analysis:"));
        messages.add(new UserMessage("Previous Analysis: " + previousJson));
        messages.add(new UserMessage("Latest Candles: " + candleJson));
        messages.add(jsonReturnFormat(periodType, marketId));

        Prompt prompt = Prompt.builder().messages(new ArrayList<>(messages)).build();

        log.info("[AI] Sending incremental {} analysis request for market {}", periodType, marketId);

        String response = chatClient.prompt(prompt)
                .options(ChatOptions.builder().build())
                .call()
                .content();

        log.info("[AI] Incremental response received for {} market {}: {}", periodType, marketId, response);

        if (response == null || response.isBlank()) {
            log.warn("[AI] Empty incremental response for {} market {}", periodType, marketId);
            return null;
        }

        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }

    private List<Message> createFirstAnalysisMessages(String marketId, List<?> dtoList, String periodType) {
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("Analyze the following " + periodType +
                " candle data for market " + marketId + "."));
        messages.add(jsonReturnFormat(periodType, marketId));
        dtoList.forEach(dto -> messages.add(new UserMessage(dto.toString())));
        return messages;
    }

    private SystemMessage jsonReturnFormat(String periodType, String marketId) {
        return new SystemMessage(String.format("""
                    STRICTLY RETURN ONLY JSON for market %s in the following structure:
                    {
                        "marketId": "%s",
                        "analysisTime": "YYYY-MM-DDTHH:MM:SSZ",
                        "momentumScore": decimal,
                        "periodStart": "YYYY-MM-DDTHH:MM:SSZ",
                        "periodEnd": "YYYY-MM-DDTHH:MM:SSZ",
                        "periodType": "%s",
                        "priceTrend": "UPWARD" | "DOWNWARD" | "SIDEWAYS",
                        "signal": "BUY" | "SELL" | "HOLD",
                        "summaryCommentENG": "string",
                        "summaryCommentKOR": "string"
                    }
                
                    IMPORTANT RULES:
                    1. ALL timestamps must be strings in ISO-8601 UTC format with 'Z' suffix.
                    2. Provide ONLY JSON, NOTHING else.
                    3. Respond ONLY in JSON. NO explanations, NO extra text, NO markdown, NO code blocks.
                    4. Perform analysis regardless of price movement or candle count.
                    5. For incremental mode, analyze only candles after the last analysis timestamp.
                    6. Treat data as completely new if marketId or periodType differs from the previous analysis.
                    7. ONLY RETURN JSON. DO NOT WRITE ANYTHING ELSE.
                """, marketId, marketId, periodType));
    }
}
