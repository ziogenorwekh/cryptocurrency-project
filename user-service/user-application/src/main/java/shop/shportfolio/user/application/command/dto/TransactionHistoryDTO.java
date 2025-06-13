package shop.shportfolio.user.application.command.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class TransactionHistoryDTO {
    private String marketId;
    private String transactionType;
    private String amount;
    private LocalDateTime transactionTime;


    public TransactionHistoryDTO(String marketId, String transactionType, String amount, LocalDateTime transactionTime) {
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }
}
