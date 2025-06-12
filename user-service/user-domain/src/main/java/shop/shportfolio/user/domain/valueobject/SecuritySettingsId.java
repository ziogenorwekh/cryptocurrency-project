package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.BaseId;

import java.util.UUID;

public class SecuritySettingsId extends BaseId<UUID> {


    public SecuritySettingsId(UUID value) {
        super(value);
    }

    @Override
    public UUID getValue() {
        return super.getValue();
    }
}
