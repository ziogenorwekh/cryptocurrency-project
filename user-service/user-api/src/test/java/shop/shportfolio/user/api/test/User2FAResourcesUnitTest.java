package shop.shportfolio.user.api.test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.user.api.resources.User2FAResources;
import shop.shportfolio.user.api.exception.UserNotAccessException;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.command.track.TrackUserTwoFactorResponse;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.util.UUID;

public class User2FAResourcesUnitTest {

    private UserApplicationService userApplicationService;
    private User2FAResources user2FAResources;

    private final UUID userId = UUID.randomUUID();
    private final UUID otherUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userApplicationService = Mockito.mock(UserApplicationService.class);
        user2FAResources = new User2FAResources(userApplicationService);
    }

    @Test
    @DisplayName("2FA 설정 조회 - 정상 호출")
    void trackUserTwoFactor_Success() {
        TrackUserTwoFactorResponse mockResponse = new TrackUserTwoFactorResponse(userId,"EMAIL",
                true);
        Mockito.when(userApplicationService.trackUserTwoFactorQuery(Mockito.any(UserTwoFactorTrackQuery.class)))
                .thenReturn(mockResponse);

        ResponseEntity<TrackUserTwoFactorResponse> response = user2FAResources.trackUserTwoFactor(userId, userId);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(mockResponse, response.getBody());
        Assertions.assertEquals(userId, response.getBody().getUserId());
        Assertions.assertEquals("EMAIL", response.getBody().getTwoFactorAuthMethod());
        Mockito.verify(userApplicationService).trackUserTwoFactorQuery(Mockito.any(UserTwoFactorTrackQuery.class));
    }

    @Test
    @DisplayName("2FA 설정 조회 - 권한 없음 예외")
    void trackUserTwoFactor_NotOwner_Throws() {
        Assertions.assertThrows(UserNotAccessException.class, () -> {
            user2FAResources.trackUserTwoFactor(otherUserId, userId);
        });
    }

    @Test
    @DisplayName("2FA 생성 - 정상 호출")
    void createUserTwoFactor_Success() {
        TwoFactorEnableCommand command = new TwoFactorEnableCommand();
        ResponseEntity<Void> response = user2FAResources.createUserTwoFactor(userId, userId, command);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());
        Assertions.assertEquals(userId, command.getUserId());
        Mockito.verify(userApplicationService).create2FASetting(command);
    }

    @Test
    @DisplayName("2FA 생성 - 권한 없음 예외")
    void createUserTwoFactor_NotOwner_Throws() {
        TwoFactorEnableCommand command = new TwoFactorEnableCommand();
        Assertions.assertThrows(UserNotAccessException.class, () -> {
            user2FAResources.createUserTwoFactor(otherUserId, userId, command);
        });
    }

    @Test
    @DisplayName("2FA 인증 코드 확인 - 정상 호출")
    void confirmUserTwoFactor_Success() {
        TwoFactorEmailVerifyCodeCommand command = new TwoFactorEmailVerifyCodeCommand();
        ResponseEntity<Void> response = user2FAResources.confirmUserTwoFactor(userId, userId, command);

        Assertions.assertEquals(204, response.getStatusCodeValue());
        Assertions.assertNull(response.getBody());
        Mockito.verify(userApplicationService).save2FA(command);
    }

    @Test
    @DisplayName("2FA 인증 코드 확인 - 권한 없음 예외")
    void confirmUserTwoFactor_NotOwner_Throws() {
        TwoFactorEmailVerifyCodeCommand command = new TwoFactorEmailVerifyCodeCommand();
        Assertions.assertThrows(UserNotAccessException.class, () -> {
            user2FAResources.confirmUserTwoFactor(otherUserId, userId, command);
        });
    }

    @Test
    @DisplayName("2FA 삭제 - 정상 호출")
    void deleteUserTwoFactor_Success() {
        ResponseEntity<TrackUserTwoFactorResponse> response = user2FAResources.deleteUserTwoFactor(userId, userId);

        Assertions.assertEquals(204, response.getStatusCodeValue());
        Mockito.verify(userApplicationService).disableTwoFactorMethod(Mockito.any(TwoFactorDisableCommand.class));
    }

    @Test
    @DisplayName("2FA 삭제 - 권한 없음 예외")
    void deleteUserTwoFactor_NotOwner_Throws() {
        Assertions.assertThrows(UserNotAccessException.class, () -> {
            user2FAResources.deleteUserTwoFactor(otherUserId, userId);
        });
    }
}
