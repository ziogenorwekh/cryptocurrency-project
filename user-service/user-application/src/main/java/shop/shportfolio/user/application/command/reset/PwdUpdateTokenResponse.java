package shop.shportfolio.user.application.command.reset;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PwdUpdateTokenResponse {

    private String token;

    @Builder
    public PwdUpdateTokenResponse(String token) {
        this.token = token;
    }

}
