package shop.shportfolio.user.application.command.create;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateCommand {
    @NotNull(message = "사용자 아이디는 필수 입력값입니다.")
    private UUID userId;

    @NotBlank(message = "사용자 이름은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "사용자 이름은 영어만 가능합니다.")
    @Size(min = 4,message = "사용자 이름은 최소 4자리 이상이어야 합니다.")
    private String username;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String phoneNumber;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|naver\\.com|hotmail\\.com)$",
//            message = "이메일 도메인은 gmail.com, naver.com, hotmail.com만 허용됩니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;
}
