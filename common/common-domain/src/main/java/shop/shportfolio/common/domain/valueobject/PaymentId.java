package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class PaymentId extends ValueObject<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
