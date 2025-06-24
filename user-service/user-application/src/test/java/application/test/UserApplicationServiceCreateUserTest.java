package application.test;


import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.exception.InvalidAuthCodeException;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthRequestCommand;
import shop.shportfolio.user.application.command.auth.UserTempEmailAuthVerifyCommand;
import shop.shportfolio.user.application.command.auth.VerifiedTempEmailUserResponse;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserDuplicationException;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceCreateUserTest {

    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private UserRepositoryAdaptor userRepositoryAdaptor;
    @Autowired
    private UserQueryHandler userQueryHandler;
    @Autowired
    private AuthCodeGenerator authCodeGenerator;
    @Autowired
    private RedisAdapter redisAdapter;

    @Autowired
    private MailSenderAdapter mailSenderAdapter;

    @Autowired
    private PasswordEncoderAdapter passwordEncoder;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@gmail.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    private final String code = "123456";
    private final String encodedPassword = "encrypedPassword";
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(userRepositoryAdaptor, redisAdapter,authCodeGenerator);

        Mockito.when(redisAdapter.saveTempEmailCode(email,code,15, TimeUnit.MINUTES)).thenReturn(code);
        Mockito.when(userRepositoryAdaptor.findByEmail(email)).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("회원가입 테스트 && 이메일 임시 인증이 안된 상태에서 회원가입 시도 테스트")
    public void createUser() {
        // given
        UserCreateCommand userCreateCommand = new UserCreateCommand(userId, username, phoneNumber, email, password);
        // when
        InvalidAuthCodeException userNotAuthenticationTemporaryEmailException =
                Assertions.assertThrows(InvalidAuthCodeException.class, () -> {
                    userApplicationService.createUser(userCreateCommand);
                });
        // then
        Mockito.verify(redisAdapter, Mockito.times(1)).isAuthenticatedTempUserId(Mockito.any());
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException);
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException.getMessage());
        Assertions.assertEquals(userNotAuthenticationTemporaryEmailException.getMessage(),
                String.format("User %s has expired email authentication", userCreateCommand.getEmail()));

        // given
        UserCreateCommand userCreateCommand2 = new UserCreateCommand(userId, username, phoneNumber, email, password);

        Mockito.when(redisAdapter.isAuthenticatedTempUserId(Mockito.any())).thenReturn(true);
        Mockito.when(userRepositoryAdaptor.save(Mockito.any())).thenReturn(testUser);
        Mockito.doReturn(testUser).when(userRepositoryAdaptor).save(Mockito.any());
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn(encodedPassword);

        // when
        UserCreatedResponse createdResponse = userApplicationService.createUser(userCreateCommand2);


        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(redisAdapter,Mockito.times(1)).deleteTempEmailCode(email);
        Assertions.assertNotNull(createdResponse);
        Assertions.assertNotNull(createdResponse.getUserId());
        Assertions.assertNotNull(createdResponse.getEmail());
        Assertions.assertNotNull(createdResponse.getPhoneNumber());
        Assertions.assertEquals(1, createdResponse.getRoles().size());
        Assertions.assertNotNull(createdResponse.getCreatedAt());
        Assertions.assertEquals(false, createdResponse.getIs2FAEnabled());
        Assertions.assertEquals("", createdResponse.getTwoFactorAuthMethod());
    }

    @Test
    @DisplayName("유저 단건 조회 테스트")
    public void findOneUser() {
        // given
        UserTrackQuery userTrackQuery = new UserTrackQuery(userId);
        Mockito.when(userRepositoryAdaptor.findByUserId(userTrackQuery.getUserId())).thenReturn(Optional.of(testUser));
        // when

        TrackUserQueryResponse queryResponse = userApplicationService.trackUserQuery(userTrackQuery);

        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Assertions.assertNotNull(queryResponse);
        Assertions.assertEquals(userId.toString(), queryResponse.getUserId());
        Assertions.assertEquals(email, queryResponse.getEmail());
        Assertions.assertEquals(username, queryResponse.getUsername());
        Assertions.assertEquals(phoneNumber, queryResponse.getPhoneNumber());
        Assertions.assertEquals(1, queryResponse.getRoles().size());
        Assertions.assertNotNull(queryResponse.getCreatedAt());
        Assertions.assertEquals(false, queryResponse.getIs2FAEnabled());
        Assertions.assertEquals("", queryResponse.getTwoFactorAuthMethod());
    }

    @Test
    @DisplayName("임시 이메일 인증 테스트")
    public void tempAuthenticationEmail() {

        // given
        UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand =
                new UserTempEmailAuthRequestCommand(email);
        Mockito.when(redisAdapter.saveTempEmailCode(email,code,15,TimeUnit.MINUTES)).thenReturn(code);
        // when
        Mockito.when(userRepositoryAdaptor.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(authCodeGenerator.generate()).thenReturn(code);
        userApplicationService.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByEmail(email);
        Mockito.verify(authCodeGenerator, Mockito.times(1)).generate();
        Mockito.verify(mailSenderAdapter,Mockito.times(1)).sendMailWithEmailAndCode(email,code);
    }

    @Test
    @DisplayName("이미 사용자가 존재하는 경우 이메일 인증을 반려하는 테스트")
    public void alreadyExistUserThenHeCannotSendMailTest() {
        // given
        UserTempEmailAuthRequestCommand userTempEmailAuthRequestCommand =
                new UserTempEmailAuthRequestCommand(email);
        Mockito.when(redisAdapter.saveTempEmailCode(email,code, 15,TimeUnit.MINUTES)).thenReturn(code);
        Mockito.when(userRepositoryAdaptor.findByEmail(email)).thenReturn(Optional.of(testUser));
        // when
        UserDuplicationException userDuplicationException = Assertions.assertThrows(UserDuplicationException.class,
                () -> { userApplicationService.sendTempEmailCodeForCreateUser(userTempEmailAuthRequestCommand);
        });
        // then

        Assertions.assertNotNull(userDuplicationException);
        Assertions.assertEquals(String.format("User with email %s already exists",email),
                userDuplicationException.getMessage());
    }

    @Test
    @DisplayName("임시 이메일 코드를 정상적으로 인증을 완료한 경우, userId를 발급하고 캐시에 저장되는 테스트")
    public void sendTempEmailCodeIfSuccessGiveUserIdAndSaveCacheTest() {
        // given
        String code = "123456";
        UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand =
                new UserTempEmailAuthVerifyCommand(email, code);
        Mockito.when(redisAdapter.verifyTempEmailAuthCode(email, code)).thenReturn(true);
        // when
        VerifiedTempEmailUserResponse verifiedTempEmailUserResponse = userApplicationService.
                verifyTempEmailCodeForCreateUser(userTempEmailAuthVerifyCommand);
        // then
        Mockito.verify(redisAdapter, Mockito.times(1)).
                verifyTempEmailAuthCode(email,code);
        Assertions.assertNotNull(verifiedTempEmailUserResponse);
        Assertions.assertNotNull(verifiedTempEmailUserResponse.getUserId());
        Assertions.assertNotNull(verifiedTempEmailUserResponse.getEmail());
    }

    @Test
    @DisplayName("이미 이메일 인증을 할 수 있는 시간이 지난 경우 예외처리 테스트")
    public void sendTempEmailCodeIfFailGiveUserIdAndSaveCacheTest() {
        // given
        String code = "123456";
        UserTempEmailAuthVerifyCommand userTempEmailAuthVerifyCommand =
                new UserTempEmailAuthVerifyCommand(email, code);
        Mockito.when(redisAdapter.verifyTempEmailAuthCode(email, code)).thenReturn(false);
        // when
        InvalidAuthCodeException userAuthExpiredException = Assertions.assertThrows(InvalidAuthCodeException.class,
                () -> userApplicationService.verifyTempEmailCodeForCreateUser(userTempEmailAuthVerifyCommand));
        // then

        Assertions.assertNotNull(userAuthExpiredException);
        Assertions.assertEquals(String.format("%s's temporal authentication is already expired",
                userTempEmailAuthVerifyCommand.getEmail()),userAuthExpiredException.getMessage());
    }
}
