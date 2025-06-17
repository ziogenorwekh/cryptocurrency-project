package shop.shportfolio.user.application.command.auth;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.LoginStep;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private final UUID userId;
    private final String token;
    private final String loginStep;

    public LoginResponse(UUID userId, String token, String loginStep) {
        this.userId = userId;
        this.token = token;
        this.loginStep = loginStep;
    }
}
