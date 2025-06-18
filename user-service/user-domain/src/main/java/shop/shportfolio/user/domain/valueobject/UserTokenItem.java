package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class UserTokenItem extends ValueObject<UUID> {

    private final String email;

    public UserTokenItem(UUID value, String email) {
        super(value);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
