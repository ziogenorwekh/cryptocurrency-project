package shop.shportfolio.user.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.exception.DomainException;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class DomainServiceTest {

    @Mock
    private UserDomainService userDomainService;
    private final String email = "test@example.com";
    private final String username = "김철수";
    private final String password = "testpwd";
    private final User mockUser = new User(new Email(email), new Username(username), new Password(password));

    @BeforeEach
    public void beforeEach() {
        Mockito.when(userDomainService.createUser(Mockito.any(Email.class), Mockito.any(Username.class),
                        Mockito.any(Password.class)))
                .thenReturn(mockUser);
    }

    @Test
    @DisplayName("유저 생성 테스트")
    public void createUserTest() {
        Email emailObj = new Email(email);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);

        userDomainService.createUser(emailObj, userObj, passwordObj);
    }

    @Test
    @DisplayName("잘못된 유저 생성 테스트 -> UserDomainServiceImpl 생성 후 테스트")
    public void wrongCreateUserTest() {
        String wrongEmail = "test";
        Email emailObj = new Email(wrongEmail);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);

        UserDomainException userDomainException1 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(emailObj, userObj, passwordObj));
        Assertions.assertNotNull(userDomainException1);
        Assertions.assertNotNull(userDomainException1.getMessage());
        Assertions.assertEquals("Invalid email.",userDomainException1.getMessage());

        String englishName = "testuser";
        Username englishNameObj = new Username(englishName);
        UserDomainException userDomainException2 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(emailObj, englishNameObj, passwordObj));
        Assertions.assertNotNull(userDomainException2);
        Assertions.assertNotNull(userDomainException2.getMessage());
        Assertions.assertEquals("Invalid username.",userDomainException2.getMessage());
    }
}
