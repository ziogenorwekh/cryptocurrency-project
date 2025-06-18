package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserOldPasswordChangeCommand {
    private UUID userId;
    private String oldPassword;
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
