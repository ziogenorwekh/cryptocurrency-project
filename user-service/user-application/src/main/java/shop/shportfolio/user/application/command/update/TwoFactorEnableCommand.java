package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorEnableCommand {

    @Setter
    private UUID userId;
    private TwoFactorAuthMethod twoFactorAuthMethod;

}
