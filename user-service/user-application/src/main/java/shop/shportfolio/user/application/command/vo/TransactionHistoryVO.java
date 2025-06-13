package shop.shportfolio.user.application.command.vo;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class TransactionHistoryVO {
    private String marketId;
    private String transactionType;
    private String amount;
    private LocalDateTime transactionTime;


    public TransactionHistoryVO(String marketId, String transactionType, String amount, LocalDateTime transactionTime) {
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }
}
