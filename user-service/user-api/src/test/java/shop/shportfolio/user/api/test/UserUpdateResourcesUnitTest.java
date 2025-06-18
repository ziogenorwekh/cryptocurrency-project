package shop.shportfolio.user.api.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.user.api.UserUpdateResources;
import shop.shportfolio.user.application.command.update.*;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserUpdateResourcesUnitTest {

    private UserApplicationService userApplicationService;
    private UserUpdateResources userUpdateResources;

    private final UUID userId = UUID.randomUUID();
    private final UUID tokenUserId = userId; // 동일하게 설정해서 isOwner 통과
    private final String password = "password";
    private final String newPassword = "newPassword";
    private final String token = "reset-token";
    private final String originalFileName = "profile.png";
    private final byte[] fileContent = new byte[]{1, 2, 3};

    @BeforeEach
    void setUp() {
        userApplicationService = Mockito.mock(UserApplicationService.class);
        userUpdateResources = new UserUpdateResources(userApplicationService);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 요청")
    public void sendEmailForResetPasswordTest() {
        UserPwdResetCommand command = new UserPwdResetCommand("email@test.com");
        ResponseEntity<Void> response = userUpdateResources.sendEmailForResetPassword(command);

        Mockito.verify(userApplicationService).sendMailResetPwd(command);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰 확인")
    public void validateResetTokenTest() {
        PwdUpdateTokenResponse expected = new PwdUpdateTokenResponse("canResetPasswordToken");
        Mockito.when(userApplicationService.validateResetTokenForPasswordUpdate(token)).thenReturn(expected);

        ResponseEntity<PwdUpdateTokenResponse> response = userUpdateResources.SendEmailForResetPassword(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    @DisplayName("비밀번호 재설정 완료")
    public void resetPasswordAfterVerificationTest() {
        UserUpdateNewPwdCommand command = new UserUpdateNewPwdCommand(token, newPassword);

        ResponseEntity<Void> response = userUpdateResources.resetPasswordAfterVerification(token, command);

        Mockito.verify(userApplicationService).setNewPasswordAfterReset(command);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("프로필 이미지 수정")
    public void updateUserProfileTest() {
        UploadUserImageCommand command = new UploadUserImageCommand(null, originalFileName, fileContent);
        UploadUserImageResponse expected = new UploadUserImageResponse(originalFileName,"url/to/profile.png");
        UploadUserImageCommand expectedCommand = new UploadUserImageCommand(userId, originalFileName, fileContent);

        Mockito.when(userApplicationService.updateUserProfileImage(expectedCommand)).thenReturn(expected);

        ResponseEntity<UploadUserImageResponse> response =
                userUpdateResources.updateUserProfile(userId, tokenUserId, command);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected.getFileUrl(), response.getBody().getFileUrl());
    }

    @Test
    @DisplayName("기존 비밀번호로 비밀번호 변경")
    public void changePasswordTest() {
        UserOldPasswordChangeCommand command = new UserOldPasswordChangeCommand(null, password, newPassword);
        UserOldPasswordChangeCommand expectedCommand = new UserOldPasswordChangeCommand(userId, password, newPassword);

        ResponseEntity<Void> response =
                userUpdateResources.changePassword(userId, tokenUserId, command);

        Mockito.verify(userApplicationService).updatePasswordWithCurrent(expectedCommand);
        assertEquals(204, response.getStatusCodeValue());
    }
}
