package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.LoginStep;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.util.UUID;

public class LoginVO extends ValueObject<UUID> {

    private final String token;
    private final LoginStep loginStep;
    public LoginVO(UUID userId, String token, LoginStep loginStep) {
        super(userId);
        this.token = token;
        this.loginStep = loginStep;
    }

    public String getToken() {
        return token;
    }

    public LoginStep getLoginStep() {
        return loginStep;
    }
}
