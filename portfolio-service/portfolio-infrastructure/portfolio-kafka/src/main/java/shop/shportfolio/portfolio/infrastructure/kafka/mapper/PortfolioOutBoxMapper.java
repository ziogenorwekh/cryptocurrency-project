package shop.shportfolio.portfolio.infrastructure.kafka.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioOutBoxMapper {

    private final ObjectMapper objectMapper;

    public PortfolioOutBoxMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serializeEvent(BaseEntity entity) {
        try {
            String serialized = objectMapper.writeValueAsString(entity);
            log.info("serialized -> {}", serialized);
            return serialized;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DepositCreatedEvent: " + e.getMessage(), e);
        }
    }

    public DepositWithdrawal deserializeDepositWithdrawal(String event) {
        try {
            log.info("deserialized before -> {}", event);

            JsonNode rootNode = objectMapper.readTree(event);

            TransactionId transactionId = new TransactionId(UUID.fromString(rootNode.path("id").path("value").asText()));
            UserId userId = new UserId(UUID.fromString(rootNode.path("userId").path("value").asText()));


            Money amount = new Money(BigDecimal.valueOf(rootNode.path("amount").path("value").asDouble()));

            TransactionType transactionType = TransactionType.valueOf(rootNode.path("transactionType").asText());
            TransactionStatus transactionStatus = TransactionStatus.valueOf(rootNode.path("transactionStatus").asText());

            TransactionTime transactionTime = new TransactionTime(
                    LocalDateTime.parse(rootNode.path("transactionTime").path("value").asText())
            );
            CreatedAt createdAt = new CreatedAt(
                    LocalDateTime.parse(rootNode.path("createdAt").path("value").asText())
            );
            UpdatedAt updatedAt = new UpdatedAt(
                    LocalDateTime.parse(rootNode.path("updatedAt").path("value").asText())
            );

            JsonNode rwaNode = rootNode.path("relatedWalletAddress");
            String walletTypeStr = rwaNode.path("walletType").asText();
            WalletType walletType = WalletType.valueOf(walletTypeStr);
            RelatedWalletAddress relatedWalletAddress = new RelatedWalletAddress(
                    rwaNode.path("value").asText(),
                    rwaNode.path("bankName").asText(),
                    walletType
            );

            return DepositWithdrawal.builder()
                    .transactionId(transactionId)
                    .userId(userId)
                    .amount(amount)
                    .transactionType(transactionType)
                    .transactionTime(transactionTime)
                    .transactionStatus(transactionStatus)
                    .relatedWalletAddress(relatedWalletAddress)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize DepositCreatedEvent: " + e.getMessage(), e);
            throw new RuntimeException("Failed to deserialize DepositCreatedEvent manually: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to deserialize DepositCreatedEvent manually: " + e.getMessage(), e);
            throw new RuntimeException("Error during manual Value Object construction: " + e.getMessage(), e);
        }
    }
}
