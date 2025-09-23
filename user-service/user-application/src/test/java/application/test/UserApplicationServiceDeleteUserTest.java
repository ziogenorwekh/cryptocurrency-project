package application.test;


import application.tmpbean.TestUserApplicationMockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.command.delete.UserDeleteCommand;
import shop.shportfolio.user.application.handler.UserCommandHandler;
import shop.shportfolio.user.application.ports.input.UserApplicationService;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryPort;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestUserApplicationMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceDeleteUserTest {

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    @Autowired
    private UserCommandHandler userCommandHandler;

    private final String username = "englishName";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepositoryPort);
    }


    @Test
    @DisplayName("유저 회원탈퇴 테스트")
    public void deleteUserTest() {
        // given
        UserDeleteCommand userDeleteCommand = new UserDeleteCommand(userId);
        Mockito.when(userRepositoryPort.findByUserId(userId)).thenReturn(Optional.of(testUser));
        // when
        userApplicationService.deleteUser(userDeleteCommand);
        // then
        Mockito.verify(userRepositoryPort,Mockito.times(1)).findByUserId(userId);
        Mockito.verify(userRepositoryPort, Mockito.times(1)).deleteUserById(userId);
    }
}
