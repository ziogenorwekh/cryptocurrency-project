package shop.shportfolio.portfolio.infrastructure.database.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OutBoxStatus;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.infrastructure.database.entity.outbox.MessageEventEntity;

import java.time.LocalDateTime;

@Component
public class PortfolioMessageEventMapper {

    public MessageEventEntity toMessageEventEntity(String topicName, String kafkaKey, String payload
    , LocalDateTime createdAt, OutBoxStatus status,String aggregateId,String aggregateType) {

        return MessageEventEntity.builder()
                .topicName(topicName)
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .kafkaKey(kafkaKey)
                .payload(payload)
                .createdAt(createdAt)
                .outBoxStatus(status)
                .build();
    }
}
