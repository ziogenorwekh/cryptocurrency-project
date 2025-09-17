package shop.shportfolio.user.application.command.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private final UUID userId;
    private final String email;
    private final String token;
    private final String loginStep;

    public LoginResponse(UUID userId, String email, String token, String loginStep) {
        this.userId = userId;
        this.email = email;
        this.token = token;
        this.loginStep = loginStep;
    }
}
