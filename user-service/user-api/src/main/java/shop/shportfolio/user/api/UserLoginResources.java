package shop.shportfolio.user.api;

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

@RestController
@RequestMapping(path = "/api/")
public class UserLoginResources {

    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserLoginResources(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginCommand loginCommand) {
        LoginResponse loginResponse = userAuthenticationService.userLogin(loginCommand);
        return ResponseEntity.ok(loginResponse);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login/2fa")
    public ResponseEntity<LoginResponse> login2FA(@RequestBody LoginTwoFactorCommand loginTwoFactorCommand) {
        LoginResponse loginResponse = userAuthenticationService.userVerify2FACode(loginTwoFactorCommand);
        return ResponseEntity.ok(loginResponse);
    }
}
