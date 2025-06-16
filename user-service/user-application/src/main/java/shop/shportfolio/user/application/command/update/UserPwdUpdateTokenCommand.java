package shop.shportfolio.user.application.command.update;


import lombok.Getter;

@Getter
public class UserPwdUpdateTokenCommand {

    private final String token;

    public UserPwdUpdateTokenCommand(String token) {
        this.token = token;
    }
}
