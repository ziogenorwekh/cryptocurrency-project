package shop.shportfolio.user.application.security;

import shop.shportfolio.common.domain.valueobject.Token;
import shop.shportfolio.common.domain.valueobject.TokenRequestType;

import java.util.UUID;

public interface JwtToken {

    Token createResetRequestPwdToken(String email, TokenRequestType tokenRequestType);

    Token createUpdatePasswordToken(UUID userId, TokenRequestType tokenRequestType);
}
