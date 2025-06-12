package application.test;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import shop.shportfolio.common.domain.valueobject.AuthCodeType;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.user.application.UserApplicationService;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotAuthenticationTemporaryEmailException;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceTest {

    @Mock
    private UserApplicationService userApplicationService;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private RedisAdapter redisAdapter;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    User testUser = User.createUser(new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));
    @BeforeEach
    public void beforeEach() {
    }
    public void afterEach() {
    }
    public void beforeAll() {
    }


    @Test
    @DisplayName("회원가입 테스트 && 이메일 임시 인증이 안된 상태에서 회원가입 시도 테스트")
    public void createUser() {
        // given
        UserCreateCommand userCreateCommand = new UserCreateCommand(username,phoneNumber,email,password);
        // 레디스에 이메일 임시 인증이 없는 경우
        Mockito.when(redisAdapter.verifyAuthCode(AuthCodeType.EMAIL_VERIFICATION,Mockito.anyString(),Mockito.anyString()))
                .thenReturn(false);
        // when

        UserNotAuthenticationTemporaryEmailException userNotAuthenticationTemporaryEmailException =
                Assertions.assertThrows(UserNotAuthenticationTemporaryEmailException.class, () -> {
            userApplicationService.createUser(userCreateCommand);
        });
        // then
        Mockito.verify(redisAdapter, Mockito.times(1)).
                verifyAuthCode(AuthCodeType.EMAIL_VERIFICATION,Mockito.anyString(),Mockito.anyString());
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException);
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException.getMessage());
        Assertions.assertEquals("User is not authentication temporary email",
                userNotAuthenticationTemporaryEmailException.getMessage());

        // given
        UserCreateCommand userCreateCommand2 = new UserCreateCommand(username,phoneNumber,email,password);
        Mockito.when(redisAdapter.verifyAuthCode(AuthCodeType.EMAIL_VERIFICATION,Mockito.anyString(),Mockito.anyString()))
                .thenReturn(true);
        // when
        UserCreatedResponse userCreatedResponse = new UserCreatedResponse(userId.toString(), username
                , phoneNumber, email, LocalDateTime.now(), testUser.getRoles().stream().map(role -> role.getRoleType().toString()).toList(),
                testUser.getSecuritySettings().getIsEnabled(), testUser.getSecuritySettings().getTwoFactorAuthMethod().toString());
        // 나머지를 부탁해

        // then
        Mockito.verify(userRepositoryAdapter, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(redisAdapter, Mockito.times(1)).
                verifyAuthCode(AuthCodeType.EMAIL_VERIFICATION,Mockito.anyString(),Mockito.anyString());
        Assertions.assertNotNull(userCreatedResponse);
        Assertions.assertNotNull(userCreatedResponse.getUserId());
        Assertions.assertNotNull(userCreatedResponse.getEmail());
        Assertions.assertNotNull(userCreatedResponse.getPhoneNumber());
        Assertions.assertEquals(1   ,userCreatedResponse.getRoles().size());
        Assertions.assertNotNull(userCreatedResponse.getCreatedAt());
        Assertions.assertEquals(userCreatedResponse.getIs2FAEnabled(),false);
        Assertions.assertEquals(userCreatedResponse.getTwoFactorAuthMethod(),"");
    }

    @Test
    @DisplayName("유저 단건 조회 테스트")
    public void findOneUser() {
        // given
        UserTrackQuery userTrackQuery = new UserTrackQuery(userId);
        Mockito.when(userRepositoryAdapter.findByUserId(userTrackQuery.getUserId())).then((Answer<?>) testUser);
        TrackUserQueryResponse trackUserQueryResponse = new TrackUserQueryResponse(userId.toString(), username
                , phoneNumber, email, LocalDateTime.now(), testUser.getRoles().stream().map(role -> role.getRoleType().toString()).toList(),
                testUser.getSecuritySettings().getIsEnabled(), testUser.getSecuritySettings().getTwoFactorAuthMethod().toString());
        // when
        Mockito.when(userApplicationService.trackUserQuery(Mockito.any(UserTrackQuery.class)))
                .thenReturn(trackUserQueryResponse);
        TrackUserQueryResponse queryResponse = userApplicationService.trackUserQuery(userTrackQuery);

        // then
        Mockito.verify(userRepositoryAdapter,Mockito.times(1)).findByUserId(Mockito.any());
        Assertions.assertNotNull(queryResponse);
        Assertions.assertEquals(queryResponse.getUserId(),userId);
        Assertions.assertEquals(queryResponse.getEmail(),email);
        Assertions.assertEquals(queryResponse.getUsername(),username);
        Assertions.assertEquals(queryResponse.getPhoneNumber(),phoneNumber);
        Assertions.assertEquals(1   ,queryResponse.getRoles().size());
        Assertions.assertNotNull(queryResponse.getCreatedAt());
        Assertions.assertEquals(queryResponse.getIs2FAEnabled(),false);
        Assertions.assertEquals(queryResponse.getTwoFactorAuthMethod(),"");
    }
}
