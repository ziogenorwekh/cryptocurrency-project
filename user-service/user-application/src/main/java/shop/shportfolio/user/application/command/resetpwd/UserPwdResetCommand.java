package shop.shportfolio.user.application.command.resetpwd;

import lombok.Getter;

@Getter
public class UserPwdResetCommand {

    private String email;

    public UserPwdResetCommand(String email) {
        this.email = email;
    }
}
