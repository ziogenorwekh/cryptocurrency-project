package shop.shportfolio.marketdata.insight.infrastructure.ai.adapter;

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
import shop.shportfolio.marketdata.insight.infrastructure.ai.config.ChatClientConfigData;
import shop.shportfolio.marketdata.insight.infrastructure.ai.mapper.OpenAiApiMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAiPortAdapter implements OpenAiPort {

    private final ChatClient chatClient;
    private final ChatClientConfigData chatClientConfigData;
    private final OpenAiApiMapper openAiApiMapper;

    @Autowired
    public OpenAiPortAdapter(ChatClient chatClient, ChatClientConfigData chatClientConfigData,
                             OpenAiApiMapper openAiApiMapper) {
        this.chatClient = chatClient;
        this.chatClientConfigData = chatClientConfigData;
        this.openAiApiMapper = openAiApiMapper;
    }

    @Override
    public AiAnalysisResponseDto analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList) {
        return analyze(dtoList,"THIRTY_MINUTES");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList) {
        return analyze(dtoList,"ONE_HOUR");
    }

    @Override
    public AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList) {
        return analyze(dtoList,"DAILY");
    }

    @Override
    public AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList) {
        return analyze(dtoList,"WEEKLY");
    }

    @Override
    public AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList) {
        return analyze(dtoList,"ONE_MONTH");
    }

    private List<Message> createMessages(List<?> dtoList, String periodType) {
        List<Message> messages = new ArrayList<>();
        messages.add(new UserMessage("Analyze the following " + periodType + " candle data."));
        messages.add(new UserMessage(String.format("""
            Return type is JSON format with the following fields:
            {
                "marketId": "string",
                "analysisTime": "YYYY-MM-DDTHH:MM:SS",
                "momentumScore": decimal,
                "periodEnd": "YYYY-MM-DDTHH:MM:SS",
                "periodStart": "YYYY-MM-DDTHH:MM:SS",
                "periodType": "%s",
                "priceTrend": "UPWARD" | "DOWNWARD" | "SIDEWAYS",
                "signal": "BUY" | "SELL" | "HOLD",
                "summaryComment": "string"
            } <- marketId symbol example is "KRW-BTC"
        """, periodType)));
        dtoList.forEach(dto -> messages.add(new UserMessage(dto.toString())));
        messages.add(new UserMessage("Please keep the response concise" +
                " and only return the JSON result without extra explanation."));
        return messages;
    }

    private AiAnalysisResponseDto analyze(List<?> dtoList, String periodType) {
        List<Message> messages = createMessages(dtoList, periodType);
        Prompt prompt = Prompt.builder().messages(messages).build();
        ChatOptions options = ChatOptions.builder().maxTokens(chatClientConfigData.getMaxTokens()).build();
        String response = chatClient.prompt(prompt).options(options).call().content();
        return openAiApiMapper.toAiAnalysisResponseDto(response);
    }
}
