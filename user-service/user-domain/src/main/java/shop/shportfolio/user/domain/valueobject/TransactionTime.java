package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDateTime;

public class TransactionTime extends ValueObject<LocalDateTime> {


    public TransactionTime(LocalDateTime value) {
        super(value);
    }
}
