package shop.shportfolio.user.application.command.create;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserCreatedResponse {

    private final String userId;
    private final String username;
    private final String phoneNumber;
    private final String email;
    private final LocalDateTime createdAt;
    private final List<String> roles;
    private final Boolean is2FAEnabled;
    private final String twoFactorAuthMethod;

    @Builder
    public UserCreatedResponse(String userId, String username, String phoneNumber, String email,
                               LocalDateTime createdAt, List<String> roles, Boolean is2FAEnabled,
                               String twoFactorAuthMethod) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
        this.roles = roles;
        this.is2FAEnabled = is2FAEnabled;
        this.twoFactorAuthMethod = twoFactorAuthMethod;
    }
}
