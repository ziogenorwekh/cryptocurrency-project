package shop.shportfolio.user.application.command.update;


import lombok.Getter;

@Getter
public class PwdUpdateTokenCommand {

    private final String token;

    public PwdUpdateTokenCommand(String token) {
        this.token = token;
    }
}
