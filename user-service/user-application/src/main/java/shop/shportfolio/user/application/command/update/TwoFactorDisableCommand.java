package shop.shportfolio.user.application.command.update;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorDisableCommand {

    @NotNull(message = "사용자 아이디는 필수 입력값입니다.")
    private UUID userId;
}
