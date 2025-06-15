package shop.shportfolio.user.application.command.update;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
public class TwoFactorEmailVerifyCodeCommand {

    private final UUID userId;
    private final TwoFactorAuthMethod twoFactorAuthMethod;
    private final String code;

    @Builder
    public TwoFactorEmailVerifyCodeCommand(UUID userId, TwoFactorAuthMethod twoFactorAuthMethod, String code) {
        this.userId = userId;
        this.twoFactorAuthMethod = twoFactorAuthMethod;
        this.code = code;
    }
}
