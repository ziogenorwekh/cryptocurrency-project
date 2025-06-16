package shop.shportfolio.user.application.dto;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
public class TransactionHistoryDTO implements Serializable {
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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionHistoryDTO that = (TransactionHistoryDTO) o;
        return Objects.equals(marketId, that.marketId) && Objects.equals(transactionType,
                that.transactionType) && Objects.equals(amount, that.amount)
                && Objects.equals(transactionTime, that.transactionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marketId, transactionType, amount, transactionTime);
    }
}
