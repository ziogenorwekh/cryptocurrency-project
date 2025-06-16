package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

public interface UserTwoFactorAuthenticationUseCase {


    void initiateTwoFactorAuth(TwoFactorEnableCommand twoFactorEnableCommand);

    void verifyTwoFactorAuthByEmail(TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand);

//    void send2faCode(String userId, String email);

//    Boolean verify2faCode(String userId, String code);
}
