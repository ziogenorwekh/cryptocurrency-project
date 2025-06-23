package shop.shportfolio.user.application.command.update;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPwdUpdateTokenCommand {


    @NotBlank(message = "토큰은 필수 입력값입니다.")
    private String token;

}
