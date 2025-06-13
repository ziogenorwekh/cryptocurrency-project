package shop.shportfolio.user.application.command.auth;

import lombok.Getter;

@Getter
public class UserTempEmailAuthVerifyCommand {

    private String email;
    private String code;

    public UserTempEmailAuthVerifyCommand(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
