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
import shop.shportfolio.user.application.command.track.TrackUserTwoFactorResponse;
import shop.shportfolio.user.application.command.track.UserTwoFactorTrackQuery;
import shop.shportfolio.user.application.command.update.TwoFactorDisableCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEmailVerifyCodeCommand;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.ports.input.UserTwoFactorAuthenticationUseCase;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.redis.RedisAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationService2FATest {

    @Autowired
    private UserRepositoryAdaptor userRepositoryAdaptor;

    @Autowired
    private MailSenderAdapter mailSenderAdapter;

    @Autowired
    private RedisAdapter redisAdapter;

    @Autowired
    private AuthCodeGenerator authCodeGenerator;

    @Autowired
    private UserTwoFactorAuthenticationUseCase userTwoFactorAuthenticationUseCase;

    @Autowired
    private UserApplicationService userApplicationService;

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@example.com";
    private final String code = "123456";

    private final User testUser = User.createUser(
            new UserId(userId),
            new Email(email),
            new PhoneNumber("01012345678"),
            new Username("김철수"),
            new Password("testpwd")
    );

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepositoryAdaptor, mailSenderAdapter, authCodeGenerator, redisAdapter);
    }

    @Test
    @DisplayName("2FA 설정 활성화 테스트")
    public void active2FASettingTest() {
        // given
        TwoFactorEnableCommand twoFactorEnableCommand = new TwoFactorEnableCommand(userId, TwoFactorAuthMethod.EMAIL);

        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(authCodeGenerator.generate()).thenReturn(code);

        // when
        userApplicationService.create2FASetting(twoFactorEnableCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(mailSenderAdapter).sendMailWithEmailAndCode(email, code);
        Mockito.verify(redisAdapter).save2FAEmailCode(email, code, 5, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("이메일 2단계 인증 코드 검증 성공 테스트")
    public void successful2FAbyEmailWithCodeTest() {
        // given
        TwoFactorEmailVerifyCodeCommand twoFactorEmailVerifyCodeCommand = new TwoFactorEmailVerifyCodeCommand(
                userId, TwoFactorAuthMethod.EMAIL, code
        );
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(authCodeGenerator.generate()).thenReturn(code);
        Mockito.when(redisAdapter.isSave2FAEmailCode(email, code)).thenReturn(true);
        // when
        userApplicationService.save2FA(twoFactorEmailVerifyCodeCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(redisAdapter, Mockito.times(1))
                .isSave2FAEmailCode(testUser.getEmail().getValue(), code);
        Mockito.verify(redisAdapter, Mockito.times(1))
                .delete2FASettingEmailCode(testUser.getEmail().getValue());
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("유저의 2FA 인증 조회 및 삭제 테스트")
    public void retrieveUserSecuritySettingTest() {
        // given
        UserTwoFactorTrackQuery userTwoFactorTrackQuery = new UserTwoFactorTrackQuery(userId);
        testUser.userSelect2FASecurityMethod(TwoFactorAuthMethod.EMAIL);
        testUser.userUse2FASecurity();
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        // when
        TrackUserTwoFactorResponse trackUserTwoFactorResponse = userApplicationService.
                trackUserTwoFactorQuery(userTwoFactorTrackQuery);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Assertions.assertNotNull(trackUserTwoFactorResponse);
        Assertions.assertNotNull(trackUserTwoFactorResponse.getUserId());
        Assertions.assertEquals(TwoFactorAuthMethod.EMAIL.name(), trackUserTwoFactorResponse.getTwoFactorAuthMethod());

        // given
        TwoFactorDisableCommand twoFactorDisableCommand = new TwoFactorDisableCommand(userId);
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        // when
        userApplicationService.disableTwoFactorMethod(twoFactorDisableCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(2)).findByUserId(userId);
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).save(Mockito.any(User.class));


    }

}