package shop.shportfolio.trading.application.dto.crypto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CryptoKafkaResponse {
    private UUID balanceId;
    private UUID userId;
    private String marketId;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
}
