package shop.shportfolio.user.application.command.update;

import lombok.Getter;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
public class TwoFactorEnableCommand {

    private final UUID userId;
    private final TwoFactorAuthMethod twoFactorAuthMethod;

    public TwoFactorEnableCommand(UUID userId, TwoFactorAuthMethod twoFactorAuthMethod) {
        this.userId = userId;
        this.twoFactorAuthMethod = twoFactorAuthMethod;
    }
}
