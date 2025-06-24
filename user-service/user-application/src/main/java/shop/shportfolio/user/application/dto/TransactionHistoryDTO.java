package shop.shportfolio.user.application.dto;

import lombok.*;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.domain.valueobject.TransactionTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDTO implements Serializable {

    private UUID userId;
    private String orderId;
    private TransactionType transactionType;
    private BigDecimal orderPrice;
    private BigDecimal quantity;
    private String marketId;
    private LocalDateTime transactionTime;


}
