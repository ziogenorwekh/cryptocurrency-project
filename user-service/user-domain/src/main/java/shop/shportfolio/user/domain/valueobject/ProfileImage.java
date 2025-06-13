package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class ProfileImage extends ValueObject<UUID> {

    private final String profileImageExtensionWithName;

    public ProfileImage(UUID value, String profileImageExtensionWithName) {
        super(value);
        this.profileImageExtensionWithName = profileImageExtensionWithName;
    }

    public String getProfileImageExtensionWithName() {
        return profileImageExtensionWithName;
    }
}
