package shop.shportfolio.user.application.ports.output.jwt;

import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenRequestType;

import java.util.UUID;

public interface JwtTokenAdapter {

    Token createResetRequestPwdToken(String email, TokenRequestType tokenRequestType);

    Token createUpdatePasswordToken(UUID userId, TokenRequestType tokenRequestType);

    Email verifyResetPwdToken(Token token);

    String getUserIdByUpdatePasswordToken(Token token);
}
