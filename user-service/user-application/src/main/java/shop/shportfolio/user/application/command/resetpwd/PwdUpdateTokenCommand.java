package shop.shportfolio.user.application.command.resetpwd;


import lombok.Getter;

@Getter
public class PwdUpdateTokenCommand {

    private String token;

    public PwdUpdateTokenCommand(String token) {
        this.token = token;
    }
}
