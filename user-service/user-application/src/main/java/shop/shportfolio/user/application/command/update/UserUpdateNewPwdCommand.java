package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateNewPwdCommand {

    private String token;
    private String newPassword;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateNewPwdCommand that = (UserUpdateNewPwdCommand) o;
        return Objects.equals(token, that.token) && Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, newPassword);
    }
}
