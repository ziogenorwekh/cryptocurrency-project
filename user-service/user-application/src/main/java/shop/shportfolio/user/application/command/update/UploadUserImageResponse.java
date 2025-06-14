package shop.shportfolio.user.application.command.update;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UploadUserImageResponse {

    private final String fileName;
    private final String fileUrl;

    @Builder
    public UploadUserImageResponse(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
