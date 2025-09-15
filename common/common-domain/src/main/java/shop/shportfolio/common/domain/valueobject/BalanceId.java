package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class BalanceId extends ValueObject<UUID> {
    public BalanceId(UUID value) {
        super(value);
    }
}
