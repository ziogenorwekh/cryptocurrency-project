package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorEnableCommand {

    private UUID userId;
    private TwoFactorAuthMethod twoFactorAuthMethod;

}
