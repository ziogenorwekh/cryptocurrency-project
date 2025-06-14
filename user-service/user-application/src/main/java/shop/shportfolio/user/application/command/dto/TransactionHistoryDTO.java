package shop.shportfolio.user.application.command.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class TransactionHistoryDTO {
    private final String marketId;
    private final String transactionType;
    private final String amount;
    private final LocalDateTime transactionTime;


    public TransactionHistoryDTO(String marketId, String transactionType, String amount, LocalDateTime transactionTime) {
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }
}
