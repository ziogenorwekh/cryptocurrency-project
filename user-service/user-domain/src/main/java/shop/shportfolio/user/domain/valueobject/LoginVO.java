package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class LoginVO extends ValueObject<UUID> {

    private final String token;
    private final String email;
    private final TokenType loginStep;
    public LoginVO(UUID userId, String token, String email, TokenType loginStep) {
        super(userId);
        this.token = token;
        this.email = email;
        this.loginStep = loginStep;
    }

    public String getToken() {
        return token;
    }

    public TokenType getTokenRequestType() {
        return loginStep;
    }

    public TokenType getLoginStep() {
        return loginStep;
    }

    public String getEmail() {
        return email;
    }
}
