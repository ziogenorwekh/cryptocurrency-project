package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
public class UploadUserImageResponse {

    private final String fileName;
    private final String fileUrl;

    @Builder
    public UploadUserImageResponse(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UploadUserImageResponse that = (UploadUserImageResponse) o;
        return Objects.equals(fileName, that.fileName) && Objects.equals(fileUrl, that.fileUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, fileUrl);
    }
}
