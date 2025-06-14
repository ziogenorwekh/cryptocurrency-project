package shop.shportfolio.user.application.command.update;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UploadUserImageCommand {
    private final UUID userId;
    private final String originalFileName;
    private final byte[] fileContent;

    @Builder
    public UploadUserImageCommand(UUID userId, String originalFileName, byte[] fileContent) {
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.fileContent = fileContent;
    }
}
