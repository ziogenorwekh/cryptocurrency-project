package shop.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class UserId extends BaseId<UUID>{
    public UserId(UUID value) {
        super(value);
    }

    @Override
    public UUID getValue() {
        return super.getValue();
    }
}
