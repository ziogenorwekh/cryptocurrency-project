package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.MessageType;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BalanceKafkaResponse {
    private final UUID userId;
    private final AssetCode assetCode;
    private final MessageType messageType;
    private final Long balance;
}
