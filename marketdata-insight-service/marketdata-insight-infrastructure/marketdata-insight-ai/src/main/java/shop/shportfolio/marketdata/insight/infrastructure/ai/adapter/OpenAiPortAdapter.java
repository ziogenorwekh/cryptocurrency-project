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
    public AiAnalysisResponseDto analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList) {
        return safeAnalyze(dtoList, "THIRTY_MINUTES");
    }

    @Override
    public AiAnalysisResponseDto analyzeThirtyMinutesWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result) {
        return safeAnalyzeWithSingle(dto, result, "THIRTY_MINUTES");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList) {
        return safeAnalyze(dtoList, "ONE_HOUR");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneHoursWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result) {
        return safeAnalyzeWithSingle(dto, result, "ONE_HOUR");
    }

    @Override
    public AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList) {
        return safeAnalyze(dtoList, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeDaysWithLatestAnalyze(CandleDayResponseDto dto, AIAnalysisResult result) {
        return safeAnalyzeWithSingle(dto, result, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList) {
        return safeAnalyze(dtoList, "WEEKLY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeksWithLatestAnalyze(CandleWeekResponseDto dto, AIAnalysisResult result) {
        return safeAnalyzeWithSingle(dto, result, "WEEKLY");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList) {
        return safeAnalyze(dtoList, "ONE_MONTH");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneMonthsWithLatestAnalyze(CandleMonthResponseDto dto, AIAnalysisResult result) {
        return safeAnalyzeWithSingle(dto, result, "ONE_MONTH");
    }

    // ---------------------------------------
    // 안전한 재시도 + 로그 + 세션 분리
    // ---------------------------------------
    private AiAnalysisResponseDto safeAnalyze(List<?> dtoList, String periodType) {
        for (int attempt = 1; attempt <= chatClientConfigData.getMaxTries(); attempt++) {
            try {
                AiAnalysisResponseDto dto = analyze(dtoList, periodType);
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

    private AiAnalysisResponseDto safeAnalyzeWithSingle(Object candleDto, AIAnalysisResult lastResult, String periodType) {
        for (int attempt = 1; attempt <= chatClientConfigData.getMaxTries(); attempt++) {
            try {
                AiAnalysisResponseDto dto = analyzeWithSingleCandle(candleDto, lastResult, periodType);
                if (dto != null) {
                    log.info("[AI] {} incremental analysis successful on attempt {}", periodType, attempt);
                    return dto;
                }
            } catch (Exception e) {
                log.error("[AI] {} incremental analysis failed on attempt {}: {}", periodType, attempt, e.getMessage(), e);
            }
        }
        log.warn("[AI] {} incremental analysis failed after {} attempts, returning empty DTO", periodType, chatClientConfigData.getMaxTries());
        return new AiAnalysisResponseDto();
    }

    private AiAnalysisResponseDto analyze(List<?> dtoList, String periodType) {
        List<Message> messages = createFirstAnalysisMessages(dtoList, periodType);

        Prompt prompt = Prompt.builder()
                .messages(new ArrayList<>(messages))
                .build();

        log.info("[AI] Sending {} candle analysis request with {} messages", periodType, messages.size());

        String response = chatClient.prompt(prompt)
                .options(ChatOptions.builder()
                        .build())
                .call()
                .content();

        log.info("[AI] Response received for {}: {}", periodType, response);

        if (response == null || response.isBlank()) {
            log.warn("[AI] Empty response for {}", periodType);
            return null;
        }

        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }

    private AiAnalysisResponseDto analyzeWithSingleCandle(Object candleDto, AIAnalysisResult lastResult, String periodType) {
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

        messages.add(new UserMessage("Analyze latest candle incrementally based on previous analysis:"));
        messages.add(new UserMessage("Previous: " + previousJson));
        messages.add(new UserMessage("Latest: " + candleJson));
        messages.add(jsonReturnFormat(periodType));
        messages.add(new SystemMessage("ONLY RETURN JSON. DO NOT WRITE ANYTHING ELSE."));

        Prompt prompt = Prompt.builder().messages(new ArrayList<>(messages)).build();

        log.info("[AI] Sending incremental {} analysis request", periodType);

        String response = chatClient.prompt(prompt)
                .options(ChatOptions.builder()
                        .build())
                .call()
                .content();

        log.info("[AI] Incremental response received for {}: {}", periodType, response);

        if (response == null || response.isBlank()) {
            log.warn("[AI] Empty incremental response for {}", periodType);
            return null;
        }
        log.info("[AI] Response received for {}: {}", periodType, response);
        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }

    private List<Message> createFirstAnalysisMessages(List<?> dtoList, String periodType) {
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("Analyze the following " + periodType + " candle data."));
        messages.add(jsonReturnFormat(periodType));
        dtoList.forEach(dto -> messages.add(new UserMessage(dto.toString())));
        messages.add(new UserMessage("ONLY RETURN JSON. DO NOT WRITE ANYTHING ELSE."));
        return messages;
    }

    private SystemMessage jsonReturnFormat(String periodType) {
        return new SystemMessage(String.format("""
        STRICTLY RETURN ONLY JSON in the following structure:
        {
            "marketId": "string",
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
        2. DO NOT provide any number, epoch millis, or local time.
        3. Provide ONLY JSON, NOTHING else.
        4. periodStart = earliest candle time, periodEnd = latest candle time.
        5. summaryCommentENG in English only.
        6. summaryCommentKOR in Korean only.
        7. Respond ONLY in JSON. NO explanations, NO extra text, NO markdown, NO code blocks.
        8. Perform analysis regardless of price movement or candle count. Always return a valid JSON according to the required structure.
        DO NOT break these rules. Any violation will be considered invalid.
        """, periodType));
    }

}
