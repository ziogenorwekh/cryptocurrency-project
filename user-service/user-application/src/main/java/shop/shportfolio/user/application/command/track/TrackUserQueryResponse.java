package shop.shportfolio.user.application.command.track;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TrackUserQueryResponse {
    private String userId;
    private String username;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private List<String> roles;
    private Boolean is2FAEnabled;
    private String twoFactorAuthMethod;

    public TrackUserQueryResponse(String userId, String username, String phoneNumber,
                                  String email, LocalDateTime createdAt, List<String> roles,
                                  Boolean is2FAEnabled, String twoFactorAuthMethod) {
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
