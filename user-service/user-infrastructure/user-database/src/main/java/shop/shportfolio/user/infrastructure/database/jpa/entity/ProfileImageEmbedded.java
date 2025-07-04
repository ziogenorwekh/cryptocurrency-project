package shop.shportfolio.user.infrastructure.database.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ProfileImageEmbedded {

    @Column(name = "PROFILE_IMAGE_ID", columnDefinition = "BINARY(16)")
    private UUID profileImageId;

    @Column(name = "FILE_URL")
    private String fileUrl;

    @Column(name = "PROFILE_IMAGE_EXTENSION")
    private String profileImageExtensionWithName;

    @Builder
    public ProfileImageEmbedded(UUID profileImageId, String fileUrl, String profileImageExtensionWithName) {
        this.profileImageId = profileImageId;
        this.fileUrl = fileUrl;
        this.profileImageExtensionWithName = profileImageExtensionWithName;
    }
}