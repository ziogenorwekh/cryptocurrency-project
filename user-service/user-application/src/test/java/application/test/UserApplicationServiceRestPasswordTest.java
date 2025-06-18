package application.test;


import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.application.exception.InvalidObjectException;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.command.update.PwdUpdateTokenResponse;
import shop.shportfolio.user.application.command.update.UserPwdUpdateTokenCommand;
import shop.shportfolio.user.application.command.update.UserUpdateNewPwdCommand;
import shop.shportfolio.user.application.command.update.UserPwdResetCommand;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdaptor;
import shop.shportfolio.user.application.ports.output.security.JwtTokenAdapter;
import shop.shportfolio.user.application.ports.output.security.PasswordEncoderAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceRestPasswordTest {


    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private UserRepositoryAdaptor userRepositoryAdaptor;

    @Autowired
    private JwtTokenAdapter jwtTokenAdapter;
    @Autowired
    private MailSenderAdapter mailSenderAdapter;


    @Autowired
    private PasswordEncoderAdapter passwordEncoder;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    private final String code = "123456";
    private final String encodedPassword = "asdeawsdp92941d.asejklcaseqjl%!@";
    private final User testUser = User.createUser(new UserId(userId), new Email(email), new PhoneNumber(phoneNumber), new Username(username)
            , new Password(password));
    private final String jwt = "JWT";
    private final Token token = new Token(jwt);
    private final String updateJwt =  "UPDATE_JWT";
    private final Token updateToken = new Token(updateJwt);
    private final String newPassword= "newpassword";

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(userRepositoryAdaptor, jwtTokenAdapter, mailSenderAdapter,passwordEncoder);
    }

    @Test
    @DisplayName("유저 비밀번호 초기화 메일 보내기 테스트")
    public void sendMailForUsersRestPasswordTest() {
        // given
        UserPwdResetCommand userPwdResetCommand = new UserPwdResetCommand(email);
        Mockito.when(userRepositoryAdaptor.findByEmail(email)).thenReturn(Optional.of(testUser));
        Mockito.when(jwtTokenAdapter.createResetRequestPwdToken(email, TokenRequestType.REQUEST_RESET_PASSWORD))
                .thenReturn(token);
        // when
        userApplicationService.sendMailResetPwd(userPwdResetCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByEmail(email);
        Mockito.verify(jwtTokenAdapter, Mockito.times(1)).createResetRequestPwdToken(email,
                TokenRequestType.REQUEST_RESET_PASSWORD);
        Mockito.verify(mailSenderAdapter, Mockito.times(1)).sendMailForResetPassword(email,
                token.getValue());
    }

    @Test
    @DisplayName("GET요청을 받고 해당 토큰을 디코딩하여 유저 비밀번호 업데이트가 가능한 토큰을 재발급")
    public void givenValidToken_whenGetRequest_thenIssuePasswordUpdateToken() {
        // given
        UserPwdUpdateTokenCommand userPwdUpdateTokenCommand = new UserPwdUpdateTokenCommand(jwt);
        Mockito.when(jwtTokenAdapter.verifyResetPwdToken(token)).thenReturn(new Email(email));
        Mockito.when(jwtTokenAdapter.createUpdatePasswordToken(userId, TokenRequestType.REQUEST_UPDATE_PASSWORD))
                .thenReturn(updateToken);
        Mockito.when(userRepositoryAdaptor.findByEmail(email)).
                thenReturn(Optional.of(testUser));
        // when
        PwdUpdateTokenResponse response = userApplicationService
                .validateResetTokenForPasswordUpdate(userPwdUpdateTokenCommand.getToken());
        // then
        Mockito.verify(jwtTokenAdapter, Mockito.times(1)).
                createUpdatePasswordToken(userId, TokenRequestType.REQUEST_UPDATE_PASSWORD);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(updateJwt, response.getToken());
    }

    @Test
    @DisplayName("최종적으로 업데이트가 가능한 토큰을 발급받고, 최종적으로 비밀번호 수정 테스트")
    public void usingUpdatePwdTokenAndChangePwdTest() {
        // given
        UserUpdateNewPwdCommand userUpdateNewPwdCommand = new UserUpdateNewPwdCommand(updateToken.getValue(),newPassword);
        // 수동으로 넣은 패스워드 값이 생성된 유저의 비밀번호와 같나요?
        Assertions.assertEquals(password,testUser.getPassword().getValue());
        Mockito.when(jwtTokenAdapter.getUserIdByUpdatePasswordToken(updateToken)).thenReturn(String.valueOf(userId));
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        // when
        userApplicationService.setNewPasswordAfterReset(userUpdateNewPwdCommand);
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(jwtTokenAdapter, Mockito.times(1)).
                getUserIdByUpdatePasswordToken(updateToken);
        Assertions.assertEquals(encodedPassword,testUser.getPassword().getValue());
    }

    @Test
    @DisplayName("업데이트할 패스워드가 기존의 패스워드와 같으면 에러 테스트")
    public void setNewPasswordAfterResetDuplicatedTest() {
        // given
        UserUpdateNewPwdCommand userUpdateNewPwdCommand = new UserUpdateNewPwdCommand(updateToken.getValue(),newPassword);
        // 수동으로 넣은 패스워드 값이 생성된 유저의 비밀번호와 같나요?
        Assertions.assertEquals(password,testUser.getPassword().getValue());
        Mockito.when(jwtTokenAdapter.getUserIdByUpdatePasswordToken(updateToken)).thenReturn(String.valueOf(userId));
        Mockito.when(userRepositoryAdaptor.findByUserId(userId)).thenReturn(Optional.of(testUser));
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(testUser.getPassword().getValue());
        Mockito.when(passwordEncoder.matches(newPassword, testUser.getPassword().getValue())).thenReturn(true);
        // when
        InvalidObjectException invalidObjectException = Assertions.assertThrows(InvalidObjectException.class, () -> {
            userApplicationService.setNewPasswordAfterReset(userUpdateNewPwdCommand);
        });
        // then
        Mockito.verify(userRepositoryAdaptor, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(jwtTokenAdapter, Mockito.times(1)).
                getUserIdByUpdatePasswordToken(updateToken);
        Assertions.assertNotNull(invalidObjectException);
        Assertions.assertNotNull(invalidObjectException.getMessage());
        Assertions.assertEquals(invalidObjectException.getMessage(),"password must not match old password");
    }
}
