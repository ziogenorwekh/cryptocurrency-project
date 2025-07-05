package application.test;


import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.common.domain.valueobject.TokenType;
import shop.shportfolio.user.domain.entity.Role;
import shop.shportfolio.user.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.generator.AuthCodeGenerator;
import shop.shportfolio.user.application.ports.input.UserAuthenticationService;
import shop.shportfolio.user.application.ports.output.mail.MailSenderPort;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;
import shop.shportfolio.user.domain.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserLoginTest {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@example.com";
    private final String password = "password";
    private final User testUser = User.createUser(
            new UserId(userId),
            new Email(email),
            new PhoneNumber("01012345678"),
            new Username("김철수"),
            new Password(password)
    );
    private final User testUser2 = User.createUser(
            new UserId(userId),
            new Email(email),
            new PhoneNumber("01012345678"),
            new Username("김철수"),
            new Password(password)
    );
    private final String accessToken = "accessToken";
    String code = "123456";
    @Autowired
    private AuthCodeGenerator authCodeGenerator;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private AuthenticatorPort authenticatorPort;

    @Autowired
    private MailSenderPort mailSenderPort;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepositoryPort, authCodeGenerator, authenticatorPort);
    }

    @Test
    @DisplayName("2FA 비활성화한 유저 로그인 테스트")
    public void disabled2FAUserLoginTest() {
        // given
        Role role = new Role(userId, RoleType.SILVER);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        LoginCommand loginCommand = new LoginCommand(email, password);
        Mockito.when(authCodeGenerator.generate()).thenReturn(code);
        Mockito.when(authenticatorPort.authenticate(email, password)).thenReturn(userId);
        Mockito.when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(testUser2));
        Mockito.when(authenticatorPort.generateLoginToken(userId, roles)).thenReturn(accessToken);
        // when
        LoginResponse loginResponse = userAuthenticationService.userLogin(loginCommand);
        // then
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(TokenType.COMPLETED.name(), loginResponse.getLoginStep());
        Assertions.assertEquals(userId, loginResponse.getUserId());
    }

    @Test
    @DisplayName("2FA 활성화한 유저 로그인 테스트")
    public void enabled2FAUserLoginTest() {
        // given
        testUser.userSelect2FASecurityMethod(TwoFactorAuthMethod.EMAIL);
        testUser.userUse2FASecurity();
        LoginCommand loginCommand = new LoginCommand(email, password);
        Mockito.when(authCodeGenerator.generate()).thenReturn(code);
        Mockito.when(authenticatorPort.authenticate(email, password)).thenReturn(userId);
        Mockito.when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(authenticatorPort.generate2FATmpToken(email)).thenReturn("tempToken");

        // when
        LoginResponse loginResponse = userAuthenticationService.userLogin(loginCommand);
        // then
        Mockito.verify(authCodeGenerator, Mockito.times(1)).generate();
        Mockito.verify(authenticatorPort, Mockito.times(1)).authenticate(email, password);
        Mockito.verify(userRepositoryPort, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(authenticatorPort, Mockito.times(1)).generate2FATmpToken(email);
        Mockito.verify(mailSenderPort, Mockito.times(1))
                .sendMailWithEmailAnd2FACode(email, code);
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(TokenType.REQUIRE_2FA.name(), loginResponse.getLoginStep());
    }
}
