package shop.shportfolio.user.application.command.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginTwoFactorCommand {

    private String tempToken;
    private String code;
}
