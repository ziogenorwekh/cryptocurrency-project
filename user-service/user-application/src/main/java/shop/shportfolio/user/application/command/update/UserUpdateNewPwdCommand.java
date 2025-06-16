package shop.shportfolio.user.application.command.update;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.TokenRequestType;

import java.util.UUID;

@Getter
public class UserUpdateNewPwdCommand {

    private final UUID userId;
    private final TokenRequestType tokenRequestType;
    private final String newPassword;

    public UserUpdateNewPwdCommand(UUID userId, TokenRequestType tokenRequestType,
                                    String newPassword) {
        this.userId = userId;
        this.tokenRequestType = tokenRequestType;
        this.newPassword = newPassword;
    }
}
