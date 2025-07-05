package shop.shportfolio.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.user.api.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.update.*;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

@Tag(name = "User Update API", description = "사용자 정보, 비밀번호, 프로필 업데이트 관련 API")
@RestController
@RequestMapping(path = "/api")
public class UserUpdateResources {

    private final UserApplicationService userApplicationService;

    public UserUpdateResources(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Operation(summary = "비밀번호 재설정 이메일 전송", description = "비밀번호 재설정을 위한 이메일을 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전송 성공")
            })
    @RequestMapping(method = RequestMethod.POST, path = "/auth/reset/password")
    public ResponseEntity<Void> sendEmailForResetPassword(@RequestBody UserPwdResetCommand userPwdResetCommand) {
        userApplicationService.sendMailResetPwd(userPwdResetCommand);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "비밀번호 재설정 토큰 검증", description = "비밀번호 재설정 토큰의 유효성을 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검증 성공",
                            content = @Content(schema = @Schema(implementation = PwdUpdateTokenResponse.class)))
            })
    @RequestMapping(method = RequestMethod.GET, path = "/auth/password/reset?token={token}")
    public ResponseEntity<PwdUpdateTokenResponse> SendEmailForResetPassword(@RequestParam("token") String token) {
        PwdUpdateTokenResponse pwdUpdateTokenResponse = userApplicationService.
                validateResetTokenForPasswordUpdate(token);
        return ResponseEntity.ok().body(pwdUpdateTokenResponse);
    }

    @Operation(summary = "비밀번호 재설정", description = "토큰 검증 후 새로운 비밀번호를 설정합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "비밀번호 재설정 성공")
            })
    @RequestMapping(method = RequestMethod.PATCH, path = "/auth/password/update")
    public ResponseEntity<Void> resetPasswordAfterVerification(@RequestHeader("X-header-Token") String token,
                                                               @RequestBody UserUpdateNewPwdCommand userUpdateNewPwdCommand) {
        userApplicationService.setNewPasswordAfterReset(new UserUpdateNewPwdCommand(token, userUpdateNewPwdCommand.getNewPassword()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 프로필 이미지 업데이트", description = "지정한 사용자의 프로필 이미지를 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "업데이트 성공",
                            content = @Content(schema = @Schema(implementation = UploadUserImageResponse.class)))
            })
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

    @Operation(summary = "사용자 비밀번호 변경", description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "변경 성공")
            })
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
