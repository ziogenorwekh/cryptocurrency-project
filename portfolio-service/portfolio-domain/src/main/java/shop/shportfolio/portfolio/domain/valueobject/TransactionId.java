package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class TransactionId extends ValueObject<UUID> {
    public TransactionId(UUID value) {
        super(value);
    }
}
