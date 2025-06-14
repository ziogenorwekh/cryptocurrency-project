package shop.shportfolio.user.application.command.update;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PwdUpdateTokenResponse {

    private final String token;

    @Builder
    public PwdUpdateTokenResponse(String token) {
        this.token = token;
    }

}
