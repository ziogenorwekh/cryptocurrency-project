package shop.shportfolio.user.application.command.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorEmailVerifyCodeCommand {

    @Setter
    @NotNull(message = "사용자 아이디는 필수 입력값입니다.")
    private UUID userId;

    @NotNull(message = "2차 인증 방식은 필수 입력값입니다.")
    private TwoFactorAuthMethod twoFactorAuthMethod;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;
}
