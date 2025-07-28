package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TransactionTime extends ValueObject<LocalDateTime> {


    public TransactionTime(LocalDateTime value) {
        super(value);
    }

    public static TransactionTime now() {
        return new TransactionTime(LocalDateTime.now(ZoneOffset.UTC));
    }
}
