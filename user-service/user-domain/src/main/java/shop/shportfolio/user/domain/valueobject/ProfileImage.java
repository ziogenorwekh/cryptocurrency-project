package shop.shportfolio.user.domain.valueobject;

import lombok.Builder;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;


public class ProfileImage extends ValueObject<UUID> {

    private final String profileImageExtensionWithName;
    private final String fileUrl;

    @Builder
    public ProfileImage(UUID value, String profileImageExtensionWithName, String fileUrl) {
        super(value);
        this.profileImageExtensionWithName = profileImageExtensionWithName;
        this.fileUrl = fileUrl;
    }

    public String getProfileImageExtensionWithName() {

        return profileImageExtensionWithName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
