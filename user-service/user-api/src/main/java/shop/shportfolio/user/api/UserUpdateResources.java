package shop.shportfolio.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.api.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.update.*;
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
    public ResponseEntity<Void> sendEmailForResetPassword(@RequestBody UserPwdResetCommand userPwdResetCommand) {
        userApplicationService.sendMailResetPwd(userPwdResetCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/password/reset?token={token}")
    public ResponseEntity<PwdUpdateTokenResponse> SendEmailForResetPassword(@RequestParam("token") String token) {
        PwdUpdateTokenResponse pwdUpdateTokenResponse = userApplicationService.
                validateResetTokenForPasswordUpdate(token);
        return ResponseEntity.ok().body(pwdUpdateTokenResponse);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/auth/password/update")
    public ResponseEntity<Void> resetPasswordAfterVerification(@RequestHeader("X-header-Token") String token,
                                                               @RequestBody UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        userApplicationService.setNewPasswordAfterReset(new UserUpdateNewPwdCommand(token, userUpdateNewPwdCommand.getNewPassword()));
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/users/profile/{userId}")
    public ResponseEntity<UploadUserImageResponse> updateUserProfile(@PathVariable("userId") UUID userId,
                                                                     @RequestHeader("X-header-Token") UUID tokenUserId,
                                                                     @RequestBody UploadUserImageCommand uploadUserImageCommand) {
        isOwner(userId, tokenUserId);
        UploadUserImageResponse uploadUserImageResponse = userApplicationService
                .updateUserProfileImage(new UploadUserImageCommand(userId,
                        uploadUserImageCommand.getOriginalFileName(), uploadUserImageCommand.getFileContent()));
        return ResponseEntity.ok().body(uploadUserImageResponse);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/users/{userId}")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID userId,
            @RequestHeader("X-header-Token") UUID tokenUserId,
            @RequestBody UserOldPasswordChangeCommand userOldPasswordChangeCommand) {
        isOwner(userId, tokenUserId);
        userApplicationService.updatePasswordWithCurrent(new UserOldPasswordChangeCommand(
                userId, userOldPasswordChangeCommand.getOldPassword(), userOldPasswordChangeCommand.getNewPassword()
        ));
        return ResponseEntity.noContent().build();
    }


    private void isOwner(UUID userId, UUID ownerId) {
        if (!userId.equals(ownerId)) {
            throw new UserNotAccessException(String.format("%s is not owner of %s", userId, ownerId));
        }
    }
}
