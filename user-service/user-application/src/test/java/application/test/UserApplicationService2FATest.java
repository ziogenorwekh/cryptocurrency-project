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
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.update.TwoFactorEnableCommand;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.ports.output.mail.MailSenderAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.TwoFactorAuthMethod;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationService2FATest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private MailSenderAdapter mailSenderAdapter;



    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    private final String code = "123456";
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));


    @Test
    @DisplayName("2FA 설정 활성화 테스트 && 2단계 인증 이메일로 설정")
    public void active2FASettingTest() {

        // given
        TwoFactorEnableCommand twoFactorEnableCommand = new TwoFactorEnableCommand(userId, TwoFactorAuthMethod.EMAIL);
        Mockito.when(userRepositoryAdapter.findByUserId(userId)).thenReturn(Optional.ofNullable(testUser));

        // when
        userApplicationService.enable2FASetting(twoFactorEnableCommand);

        // then
        Mockito.verify(userRepositoryAdapter, Mockito.times(1)).findByUserId(userId);
        Mockito.verify(userRepositoryAdapter, Mockito.times(1)).save(testUser);
        Mockito.verify(mailSenderAdapter, Mockito.times(1)).sendMailWithEmailAndCode(email, code);
    }
}
