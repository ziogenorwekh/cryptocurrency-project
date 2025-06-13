package shop.shportfolio.user.application.command.auth;

import lombok.Getter;

@Getter
public class UserTempEmailAuthRequestCommand {

    private String email;

    public UserTempEmailAuthRequestCommand(String email) {
        this.email = email;
    }
}
