package shop.shportfolio.user.application.ports.input;

import shop.shportfolio.common.domain.valueobject.Token;

import java.util.UUID;

public interface PasswordResetUseCase {

    Token sendMailResetPwd(String email);
    Token validateResetTokenForPasswordUpdate(String pwdUpdateToken);
    UUID getTokenByUserIdForUpdatePassword(String token);
}
