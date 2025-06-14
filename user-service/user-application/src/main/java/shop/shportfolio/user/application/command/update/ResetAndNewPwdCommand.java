package shop.shportfolio.user.application.command.update;

import lombok.Getter;

@Getter
public class ResetAndNewPwdCommand {

    private final String newPassword;
    private final String token;

    public ResetAndNewPwdCommand(String newPassword, String token) {
        this.newPassword = newPassword;
        this.token = token;
    }
}
