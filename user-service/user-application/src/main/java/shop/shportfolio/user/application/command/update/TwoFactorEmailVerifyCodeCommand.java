package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorEmailVerifyCodeCommand {

    private UUID userId;
    private TwoFactorAuthMethod twoFactorAuthMethod;
    private String code;

}
