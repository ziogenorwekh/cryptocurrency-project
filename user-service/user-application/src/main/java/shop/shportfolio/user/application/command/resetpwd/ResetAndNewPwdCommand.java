package shop.shportfolio.user.application.command.resetpwd;

import lombok.Getter;

@Getter
public class ResetAndNewPwdCommand {

    private String newPassword;
    private String token;

    public ResetAndNewPwdCommand(String newPassword, String token) {
        this.newPassword = newPassword;
        this.token = token;
    }
}
