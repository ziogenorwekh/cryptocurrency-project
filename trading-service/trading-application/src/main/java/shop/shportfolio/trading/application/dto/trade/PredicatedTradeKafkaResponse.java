package shop.shportfolio.trading.application.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class PredicatedTradeKafkaResponse {
    private final String tradeId;
    private final String userId;
    private final String marketId;
    private final String buyOrderId;
    private final String sellOrderId;
    private final String orderPrice;
    private final String quantity;
    private final Instant createdAt;
    private final TransactionType transactionType;
    private final MessageType messageType;
}
