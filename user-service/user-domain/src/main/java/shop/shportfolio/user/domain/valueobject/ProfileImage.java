package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class ProfileImage extends ValueObject<UUID> {

    private final String profileImageExtension;

    public ProfileImage(UUID value, String profileImageExtension) {
        super(value);
        this.profileImageExtension = profileImageExtension;
    }

    public String getProfileImageExtension() {
        return profileImageExtension;
    }
}
