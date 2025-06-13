package application.test;


import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.application.UserApplicationService;
import shop.shportfolio.user.application.command.UserPwdResetCommand;
import shop.shportfolio.user.application.handler.UserQueryHandler;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.application.security.JwtToken;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceRestPasswordTest {


    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private UserQueryHandler userQueryHandler;

    @Autowired
    private JwtToken jwtToken;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    private final String code = "123456";

    private final User testUser = User.createUser(new UserId(userId), new Email(email), new PhoneNumber(phoneNumber), new Username(username)
            , new Password(password));
    private final Token token = new Token("JWT");
    @Autowired
    private MailSenderAdapter mailSenderAdapter;


    @Test
    @DisplayName("유저 비밀번호 초기화 메일 보내기 테스트")
    public void sendMailForUsersRestPasswordTest() {
        // given
        UserPwdResetCommand userPwdResetCommand = new UserPwdResetCommand(email);
        Mockito.when(userRepositoryAdapter.findByEmail(email)).thenReturn(Optional.of(testUser));
        Mockito.when(jwtToken.createResetRequestPwdToken(email, TokenRequestType.REQUEST_RESET_PASSWORD))
                .thenReturn(token);
        // when
        userApplicationService.sendMailResetPwd(userPwdResetCommand);
        // then
        Mockito.verify(userRepositoryAdapter, Mockito.times(1)).findByEmail(email);
        Mockito.verify(jwtToken, Mockito.times(1)).createResetRequestPwdToken(email,
                TokenRequestType.REQUEST_RESET_PASSWORD);
        Mockito.verify(mailSenderAdapter, Mockito.times(1)).sendMailForResetPassword(email,
                token.getValue());
    }

    @Test
    @DisplayName("GET요청을 받고 해당 토큰을 디코딩하여 유저 비밀번호 업데이트가 가능한 토큰을 재발급")
    public void
}
