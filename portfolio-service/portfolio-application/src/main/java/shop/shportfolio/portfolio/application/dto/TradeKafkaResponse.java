package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class TradeKafkaResponse {
    private final String tradeId;
    private final String marketId;
    private final String userId;
    private final String buyOrderId;
    private final String sellOrderId;
    private final double orderPrice;
    private final double quantity;
    private final TransactionType transactionType;
    private final Instant createdAt;

    @Override
    public String toString() {
        return "TradeKafkaResponse{" +
                "tradeId='" + tradeId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", userId='" + userId + '\'' +
                ", buyOrderId='" + buyOrderId + '\'' +
                ", sellOrderId='" + sellOrderId + '\'' +
                ", orderPrice=" + orderPrice +
                ", quantity=" + quantity +
                ", transactionType=" + transactionType +
                ", createdAt=" + createdAt +
                '}';
    }
}
