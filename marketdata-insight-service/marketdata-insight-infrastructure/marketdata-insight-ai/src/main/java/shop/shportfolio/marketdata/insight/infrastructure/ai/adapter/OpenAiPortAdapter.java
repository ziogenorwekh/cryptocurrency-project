package shop.shportfolio.marketdata.insight.infrastructure.ai.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.infrastructure.ai.config.ChatClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.ai.mapper.OpenAiApiMapper;

import java.util.ArrayList;
import java.util.List;

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
        return analyze(dtoList, "THIRTY_MINUTES");
    }

    @Override
    public AiAnalysisResponseDto analyzeThirtyMinutesWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result) {
        return analyzeWithSingleCandle(dto, result, "THIRTY_MINUTES");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList) {
        return analyze(dtoList, "ONE_HOUR");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneHoursWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result) {
        return analyzeWithSingleCandle(dto, result, "ONE_HOUR");
    }

    @Override
    public AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList) {
        return analyze(dtoList, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeDaysWithLatestAnalyze(CandleDayResponseDto dto, AIAnalysisResult result) {
        return analyzeWithSingleCandle(dto, result, "DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList) {
        return analyze(dtoList, "WEEKLY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeksWithLatestAnalyze(CandleWeekResponseDto dto, AIAnalysisResult result) {
        return analyzeWithSingleCandle(dto, result, "WEEKLY");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList) {
        return analyze(dtoList, "ONE_MONTH");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneMonthsWithLatestAnalyze(CandleMonthResponseDto dto, AIAnalysisResult result) {
        return analyzeWithSingleCandle(dto, result, "ONE_MONTH");
    }



    private List<Message> createFirstAnalysisMessages(List<?> dtoList, String periodType) {
        List<Message> messages = new ArrayList<>();

        messages.add(new UserMessage("Analyze the following " + periodType + " candle data."));
        messages.add(jsonReturnFormat(periodType));

        dtoList.forEach(dto -> messages.add(new UserMessage(dto.toString())));
        messages.add(new UserMessage("ONLY RETURN JSON. DO NOT WRITE ANYTHING ELSE."));

        return messages;
    }

    private AiAnalysisResponseDto analyzeWithSingleCandle(Object candleDto,
                                                          AIAnalysisResult lastResult,
                                                          String periodType) {
        List<Message> messages = new ArrayList<>();

        // 이전 분석 결과를 JSON으로 직렬화
        AiAnalysisResponseDto previousAnalysisDto = openAiApiMapper.aiAnalysisResultToAiAnalysisResult(lastResult);
        String previousAnalysisJson;
        try {
            previousAnalysisJson = objectMapper.writeValueAsString(previousAnalysisDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize previous analysis to JSON", e);
        }

        // 최신 캔들 데이터 JSON 직렬화
        String latestCandleJson;
        try {
            latestCandleJson = objectMapper.writeValueAsString(candleDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize candle DTO to JSON", e);
        }

        // AI 메시지 구성
        messages.add(new UserMessage("Analyze the following latest candle data incrementally based on previous analysis:"));
        messages.add(new UserMessage("Previous analysis result (JSON): " + previousAnalysisJson));
        messages.add(new UserMessage("Latest candle data (JSON): " + latestCandleJson));

        // JSON 구조 강제
        messages.add(jsonReturnFormat(periodType));

        messages.add(new UserMessage("ONLY RETURN JSON. DO NOT WRITE ANYTHING ELSE."));

        Prompt prompt = Prompt.builder().messages(messages).build();
        ChatOptions options = ChatOptions.builder().maxTokens(chatClientConfigData.getMaxTokens()).build();
        String response = chatClient.prompt(prompt).options(options).call().content();

        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }


    private AiAnalysisResponseDto analyze(List<?> dtoList, String periodType) {
        List<Message> messages = createFirstAnalysisMessages(dtoList, periodType);
        Prompt prompt = Prompt.builder().messages(messages).build();
        ChatOptions options = ChatOptions.builder().maxTokens(chatClientConfigData.getMaxTokens()).build();
        String response = chatClient.prompt(prompt).options(options).call().content();
        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }

    private UserMessage jsonReturnFormat(String periodType) {
        return new UserMessage(String.format("""
                STRICTLY RETURN ONLY JSON in the following structure:
                {
                    "marketId": "string",
                    "analysisTime": "YYYY-MM-DDTHH:MM:SS",
                    "momentumScore": decimal,
                    "periodStart": "YYYY-MM-DDTHH:MM:SS",
                    "periodEnd": "YYYY-MM-DDTHH:MM:SS",
                    "periodType": "%s",
                    "priceTrend": "UPWARD" | "DOWNWARD" | "SIDEWAYS",
                    "signal": "BUY" | "SELL" | "HOLD",
                    "summaryComment": "string"
                }
                Respond strictly in JSON format only, without any explanation, notes, or extra text.
                """, periodType));
    }
}
