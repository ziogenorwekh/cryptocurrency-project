package shop.shportfolio.user.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.exception.DomainException;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.ProfileImage;
import shop.shportfolio.user.domain.valueobject.Username;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class DomainServiceTest {

    //    @Mock
    private UserDomainServiceImpl userDomainService;
    private final String email = "test@example.com";
    private final String username = "김철수";
    private final String password = "testpwd";
    private final String phoneNumber = "123456789";
    private final User mockUser = new User(new Email(email), new PhoneNumber(phoneNumber), new Username(username), new Password(password));
    private final User mockUser2 = new User(new Email(email), new PhoneNumber(phoneNumber), new Username(username), new Password(password));
    private final User mockUser3ByUserStaticLogic = User.createUser(new  Email(email), new PhoneNumber(phoneNumber),
            new Username(username), new Password(password));
    private final UUID newProfileImageId = UUID.randomUUID();



    @BeforeEach
    public void beforeEach() {
        userDomainService = new UserDomainServiceImpl();
//        Mockito.when(userDomainService.createUser(Mockito.any(Email.class), Mockito.any(Username.class),
//                        Mockito.any(Password.class)))
//                .thenReturn(mockUser);
    }

    @Test
    @DisplayName("유저 생성 테스트")
    public void createUserTest() {
        Email emailObj = new Email(email);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);
        PhoneNumber phoneNumberObj = new PhoneNumber(phoneNumber);
        userDomainService.createUser(emailObj, phoneNumberObj, userObj, passwordObj);
    }

    @Test
    @DisplayName("잘못된 유저 생성 테스트 -> UserDomainServiceImpl 생성 후 테스트")
    public void wrongCreateUserTest() {
        String wrongEmail = "test";
        Email emailObj = new Email(wrongEmail);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);
        PhoneNumber phoneNumberObj = new PhoneNumber(phoneNumber);
        UserDomainException userDomainException1 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(emailObj, phoneNumberObj, userObj, passwordObj));
        Assertions.assertNotNull(userDomainException1);
        Assertions.assertNotNull(userDomainException1.getMessage());
        Assertions.assertEquals("Invalid email.", userDomainException1.getMessage());

        String englishName = "testuser";
        Email emailObj2 = new Email(email);
        Username englishNameObj = new Username(englishName);
        UserDomainException userDomainException2 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(emailObj2, phoneNumberObj, englishNameObj, passwordObj));
        Assertions.assertNotNull(userDomainException2);
        Assertions.assertNotNull(userDomainException2.getMessage());
        Assertions.assertEquals("Invalid username.", userDomainException2.getMessage());
    }

    @Test
    @DisplayName("패스워드 리셋 로직 테스트")
    public void resetPasswordTest() {
        String newPassword = "newpassword";
        Password newPasswordObj = new Password(newPassword);

        userDomainService.updatePassword(mockUser, newPasswordObj);

        UserDomainException userDomainException = Assertions.assertThrows(UserDomainException.class, () ->
                userDomainService.updatePassword(mockUser2, new Password(password)));

        Assertions.assertNotNull(userDomainException);
        Assertions.assertNotNull(userDomainException.getMessage());
        Assertions.assertEquals("Passwords is matched by before password.", userDomainException.getMessage());
    }

    @Test
    @DisplayName("프로필 이미지 변경 로직 테스트")
    public void changePasswordTest() {
        ProfileImage profileImage = new ProfileImage(newProfileImageId,"newImage");
        userDomainService.updateProfileImage(mockUser3ByUserStaticLogic, profileImage);

        Assertions.assertEquals(mockUser3ByUserStaticLogic.getProfileImage(), profileImage);
        Assertions.assertEquals("newImage", mockUser3ByUserStaticLogic.getProfileImage().
                getProfileImageExtension());
    }
}
