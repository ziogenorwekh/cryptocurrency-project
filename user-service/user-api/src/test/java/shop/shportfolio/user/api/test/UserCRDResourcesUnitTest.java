package shop.shportfolio.user.api.test;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.common.exception.UserNotAccessException;
import shop.shportfolio.user.api.resources.UserCRDResources;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.*;
import shop.shportfolio.user.application.ports.input.UserApplicationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;


public class UserCRDResourcesUnitTest {

    private UserApplicationService userApplicationService;
    private UserCRDResources userCRDResources;

    private final UUID userId = UUID.randomUUID();
    private final String username = "username";
    private final String email = "email@test.com";
    private final String phoneNumber = "01012345678";
    private final String password = "password";
    private final LocalDateTime fixedNow = LocalDateTime.of(2025, 6, 17, 12, 0);
    @BeforeEach
    void setUp() {
        userApplicationService = Mockito.mock(UserApplicationService.class);
        userCRDResources = new UserCRDResources(userApplicationService);
    }

    @Test
    @DisplayName("유저 생성 테스트")
    void createUserTest() {
        // given
        UserCreateCommand command = new UserCreateCommand(
                userId,
                username,
                email,
                phoneNumber,
                password
        );

        UserCreatedResponse expectedResponse = new UserCreatedResponse(
                userId.toString(),
                username,
                phoneNumber,
                email,
                fixedNow,
                new ArrayList<>(),
                false,
                ""
        );
        Mockito.when(userApplicationService.createUser(any(UserCreateCommand.class))).thenReturn(expectedResponse);

        // when
        // 컨트롤러 메서드 직접 호출 (예: createUser가 ResponseEntity<UserCreatedResponse> 반환한다고 가정)
        ResponseEntity<UserCreatedResponse> responseEntity = userCRDResources.createUser(command);

        // then
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(201, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(expectedResponse.getUserId(), responseEntity.getBody().getUserId());
    }

    @Test
    @DisplayName("유저 생성 임시 토큰을 위한 이메일 전송 테스트")
    public void sendEmailTempCreateUserTest() {
        UserTempEmailAuthRequestCommand command = new UserTempEmailAuthRequestCommand(email);
        ResponseEntity<Void> voidResponseEntity = userCRDResources.SendEmailTempCreateUser(command);

        Assertions.assertNotNull(voidResponseEntity);
        Assertions.assertEquals(200, voidResponseEntity.getStatusCodeValue());
    }

    @Test
    @DisplayName("유저 임시 이메일 인증 코드 검증 테스트")
    public void verifyUserEmailCodeTest() {
        UserTempEmailAuthVerifyCommand command = new UserTempEmailAuthVerifyCommand("temp-token", "123456");
        var expectedResponse = Mockito.mock(VerifiedTempEmailUserResponse.class);
        Mockito.when(userApplicationService.verifyTempEmailCodeForCreateUser(any(UserTempEmailAuthVerifyCommand.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<?> responseEntity = userCRDResources.verifyUserEmailCode(command);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(202, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    @DisplayName("유저 조회 테스트 - 성공")
    public void retrieveUserTest() {
        UUID tokenUserId = userId;
        TrackUserQueryResponse expectedResponse = Mockito.mock(TrackUserQueryResponse.class);

        Mockito.when(userApplicationService.trackUserQuery(any(UserTrackQuery.class))).thenReturn(expectedResponse);

        ResponseEntity<TrackUserQueryResponse> responseEntity = userCRDResources.retrieveUser(tokenUserId);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(200, responseEntity.getStatusCodeValue());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }


    @Test
    @DisplayName("유저 삭제 테스트 - 성공")
    public void deleteUserTest() {
        UUID tokenUserId = userId;
        ResponseEntity<Void> responseEntity = userCRDResources.deleteUser(tokenUserId);

        Mockito.verify(userApplicationService).deleteUser(Mockito.argThat(cmd ->
                cmd.getUserId().equals(userId)
        ));
        Assertions.assertEquals(204, responseEntity.getStatusCodeValue());
    }


}