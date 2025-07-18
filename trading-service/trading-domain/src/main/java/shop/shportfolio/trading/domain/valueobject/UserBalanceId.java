package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class UserBalanceId extends ValueObject<UUID> {
    public UserBalanceId(UUID value) {
        super(value);
    }
}
