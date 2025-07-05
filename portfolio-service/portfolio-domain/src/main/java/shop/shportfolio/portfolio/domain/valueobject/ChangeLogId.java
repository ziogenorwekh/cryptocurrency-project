package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class ChangeLogId extends ValueObject<UUID> {
    public ChangeLogId(UUID value) {
        super(value);
    }
}
