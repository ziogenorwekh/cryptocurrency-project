package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class TransactionId extends ValueObject<UUID> {
    public TransactionId(UUID value) {
        super(value);
    }
}
