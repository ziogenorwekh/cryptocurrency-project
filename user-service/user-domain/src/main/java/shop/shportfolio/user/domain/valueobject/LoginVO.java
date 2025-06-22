package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class LoginVO extends ValueObject<UUID> {

    private final String token;
    private final TokenType loginStep;
    public LoginVO(UUID userId, String token, TokenType loginStep) {
        super(userId);
        this.token = token;
        this.loginStep = loginStep;
    }

    public String getToken() {
        return token;
    }

    public TokenType getTokenRequestType() {
        return loginStep;
    }
}
