package shop.shportfolio.user.application.command.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTempEmailAuthVerifyCommand {

    private String email;
    private String code;
}
