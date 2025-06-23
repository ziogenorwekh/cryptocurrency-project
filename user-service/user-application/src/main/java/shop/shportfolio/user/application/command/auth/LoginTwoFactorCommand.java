package shop.shportfolio.user.application.command.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginTwoFactorCommand {

    @NotBlank(message = "임시 토큰은 필수 입력값입니다.")
    private String tempToken;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;
}
