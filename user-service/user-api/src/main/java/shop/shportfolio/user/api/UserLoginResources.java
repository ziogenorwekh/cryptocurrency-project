package shop.shportfolio.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.application.ports.input.UserAuthenticationService;

@Tag(name = "User Login API", description = "사용자 로그인 및 2단계 인증 로그인 API")
@RestController
@RequestMapping(path = "/api/")
public class UserLoginResources {

    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserLoginResources(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Operation(summary = "사용자 로그인", description = "사용자 자격 증명을 통해 로그인을 시도합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
            })
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginCommand loginCommand) {
        LoginResponse loginResponse = userAuthenticationService.userLogin(loginCommand);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "사용자 2FA 로그인", description = "2단계 인증 코드를 통해 로그인을 완료합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "2FA 로그인 성공",
                            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
            })
    @RequestMapping(method = RequestMethod.POST, path = "/login/2fa")
    public ResponseEntity<LoginResponse> login2FA(@RequestBody LoginTwoFactorCommand loginTwoFactorCommand) {
        LoginResponse loginResponse = userAuthenticationService.userVerify2FACode(loginTwoFactorCommand);
        return ResponseEntity.ok(loginResponse);
    }
}
