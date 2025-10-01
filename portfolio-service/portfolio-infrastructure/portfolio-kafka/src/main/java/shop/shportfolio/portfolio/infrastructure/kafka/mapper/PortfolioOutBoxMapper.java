package shop.shportfolio.portfolio.infrastructure.kafka.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;

public class PortfolioOutBoxMapper {

    private final ObjectMapper objectMapper;

    public PortfolioOutBoxMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serializeEvent(DepositWithdrawal event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DepositCreatedEvent: " + e.getMessage(), e);
        }
    }
}
