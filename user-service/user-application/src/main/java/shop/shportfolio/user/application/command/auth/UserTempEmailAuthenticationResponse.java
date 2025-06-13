package shop.shportfolio.user.application.command.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserTempEmailAuthenticationResponse {

    private String code;

    @Builder
    public UserTempEmailAuthenticationResponse(String code) {
        this.code = code;
    }
}
