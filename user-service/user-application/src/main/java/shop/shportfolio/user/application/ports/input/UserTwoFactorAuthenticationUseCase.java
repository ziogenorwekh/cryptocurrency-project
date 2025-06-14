package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

public interface UserTwoFactorAuthenticationUseCase {


    void update2FASetting(UUID userId, TwoFactorAuthMethod twoFactorAuthMethod);



    void send2faCode(String userId, String email);

    Boolean verify2faCode(String userId, String code);
}
