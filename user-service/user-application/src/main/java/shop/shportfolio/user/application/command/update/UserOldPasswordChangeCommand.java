package shop.shportfolio.user.application.command.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserOldPasswordChangeCommand {
    @NotNull(message = "사용자 아이디는 필수 입력값입니다.")
    private UUID userId;

    @NotBlank(message = "기존 비밀번호는 필수 입력값입니다.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    private String newPassword;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserOldPasswordChangeCommand that = (UserOldPasswordChangeCommand) o;
        return Objects.equals(userId, that.userId) && Objects.equals(oldPassword, that.oldPassword) && Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, oldPassword, newPassword);
    }
}
