package shop.shportfolio.user.application.command.update;

import lombok.Getter;

@Getter
public class UserPwdResetCommand {

    private final String email;

    public UserPwdResetCommand(String email) {
        this.email = email;
    }
}
