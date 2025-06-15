package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

public interface UserTwoFactorAuthenticationUseCase {


    void initiateTwoFactorAuth(UUID userId, TwoFactorAuthMethod twoFactorAuthMethod);

    void verifyTwoFactorAuthByEmail(UUID userID, String code);

//    void send2faCode(String userId, String email);

//    Boolean verify2faCode(String userId, String code);
}
