package shop.shportfolio.user.application.command.update;

import lombok.Getter;

@Getter
public class UserUpdateNewPwdCommand {

    private final String newPassword;
    private final String token;

    public UserUpdateNewPwdCommand(String newPassword, String token) {
        this.newPassword = newPassword;
        this.token = token;
    }
}
