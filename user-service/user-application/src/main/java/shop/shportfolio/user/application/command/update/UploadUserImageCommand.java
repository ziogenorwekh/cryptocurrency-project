package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadUserImageCommand {
    private UUID userId;
    private String originalFileName;
    private byte[] fileContent;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UploadUserImageCommand that = (UploadUserImageCommand) o;
        return Objects.equals(userId, that.userId) && Objects.equals(originalFileName, that.originalFileName) && Objects.deepEquals(fileContent, that.fileContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, originalFileName, Arrays.hashCode(fileContent));
    }
}
