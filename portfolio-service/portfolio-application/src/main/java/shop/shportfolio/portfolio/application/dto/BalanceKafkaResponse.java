package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.DirectionType;
import shop.shportfolio.common.domain.valueobject.MessageType;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BalanceKafkaResponse {
    private final UUID userId;
    private final AssetCode assetCode;
    private final DirectionType direction;
    private final MessageType messageType;
    private final Long amount;

    @Override
    public String toString() {
        return "BalanceKafkaResponse{" +
                "userId=" + userId +
                ", assetCode=" + assetCode +
                ", messageType=" + messageType +
                ", directionType = " + direction +
                ", amount=" + amount +
                '}';
    }
}
