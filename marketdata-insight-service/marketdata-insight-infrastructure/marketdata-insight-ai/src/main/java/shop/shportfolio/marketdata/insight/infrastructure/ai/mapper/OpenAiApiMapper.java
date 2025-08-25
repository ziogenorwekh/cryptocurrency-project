package shop.shportfolio.marketdata.insight.infrastructure.ai.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;

@Component
public class OpenAiApiMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public OpenAiApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public AiAnalysisResponseDto toAiAnalysisResponseDto(String response) {
        return objectMapper.convertValue(response, AiAnalysisResponseDto.class);

    }
}
