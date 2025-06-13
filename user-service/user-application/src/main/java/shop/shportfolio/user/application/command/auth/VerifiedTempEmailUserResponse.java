package shop.shportfolio.user.application.command.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class VerifiedTempEmailUserResponse {


    private UUID userId;
    private String email;

    @Builder
    public VerifiedTempEmailUserResponse(UUID userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
