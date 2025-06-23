package shop.shportfolio.user.application.command.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "사용자 아이디는 필수 입력값입니다.")
    private UUID userId;
    @NotBlank(message = "파일명은 필수 입력값입니다.")
    private String originalFileName;

    @NotNull(message = "파일 내용은 필수 입력값입니다.")
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
