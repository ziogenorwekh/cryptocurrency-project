package shop.shportfolio.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.application.command.update.PwdUpdateTokenResponse;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api")
public class UserUpdateResources {

    private final UserApplicationService userApplicationService;

    public UserUpdateResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/auth/reset/password")
    public ResponseEntity<Void> SendEmailForResetPassword(@RequestBody UserPwdResetCommand userPwdResetCommand) {
        userApplicationService.sendMailResetPwd(userPwdResetCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/password/reset?token={token}")
    public ResponseEntity<PwdUpdateTokenResponse> SendEmailForResetPassword(@PathVariable String token) {
        PwdUpdateTokenResponse pwdUpdateTokenResponse = userApplicationService.
                validateResetTokenForPasswordUpdate(token);
        return ResponseEntity.ok().body(pwdUpdateTokenResponse);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/auth/password/update")
    public ResponseEntity<Void> updateUserPassword(@RequestHeader("X-header-User-Id")UUID userId,
                                                   @RequestHeader("X-header-Token-Type") TokenRequestType tokenRequestType,
                                                   @RequestBody UserUpdateNewPwdCommand userUpdateNewPwdCommand) {

    }


}
