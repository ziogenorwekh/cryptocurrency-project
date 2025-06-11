package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.BaseId;

import java.util.UUID;

public class RoleId extends BaseId<UUID> {
    public RoleId(UUID value) {
        super(value);
    }

    @Override
    public UUID getValue() {
        return super.getValue();
    }
}
