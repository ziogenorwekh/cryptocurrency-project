package shop.shportfolio.user.application.ports.output.security;

import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.Token;
import shop.shportfolio.user.domain.valueobject.TokenRequestType;

import java.util.UUID;

public interface JwtTokenAdapter {

    Token createResetRequestPwdToken(String email, TokenRequestType tokenRequestType);

    Token createUpdatePasswordToken(UUID userId, TokenRequestType tokenRequestType);

    Email verifyResetPwdToken(Token token);

    String getUserIdByUpdatePasswordToken(Token token);
}
