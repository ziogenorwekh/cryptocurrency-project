package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class TransactionTime extends ValueObject<LocalDateTime> {


    public TransactionTime(LocalDateTime value) {
        super(value);
    }
}
