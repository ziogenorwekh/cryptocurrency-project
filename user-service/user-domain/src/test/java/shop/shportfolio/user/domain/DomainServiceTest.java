package shop.shportfolio.user.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.user.domain.valueobject.Email;
import shop.shportfolio.user.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.exception.UserDomainException;
import shop.shportfolio.user.domain.valueobject.*;

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
    private final UserId userId = new UserId(UUID.randomUUID());
    private final User mockUser = User.createUser(userId, new Email(email), new PhoneNumber(phoneNumber), new Username(username), new Password(password));
    private final User mockUser3ByUserStaticLogic = User.createUser(userId,new Email(email), new PhoneNumber(phoneNumber),
            new Username(username), new Password(password));
    private final UUID newProfileImageId = UUID.randomUUID();


    @BeforeAll
    public void before() {
        userDomainService = new UserDomainServiceImpl();
    }

    @Test
    @DisplayName("유저 생성 테스트")
    public void createUserTest() {
        // given
        Email emailObj = new Email(email);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);
        PhoneNumber phoneNumberObj = new PhoneNumber(phoneNumber);
        // when && then
        userDomainService.createUser(userId, emailObj, phoneNumberObj, userObj, passwordObj);
    }

    @Test
    @DisplayName("잘못된 유저 생성 테스트 -> UserDomainServiceImpl 생성 후 테스트")
    public void wrongCreateUserTest() {
        // given
        String wrongEmail = "test";
        Email emailObj = new Email(wrongEmail);
        Username userObj = new Username(username);
        Password passwordObj = new Password(password);
        PhoneNumber phoneNumberObj = new PhoneNumber(phoneNumber);

        String englishName = "testuser";
        Email emailObj2 = new Email(email);
        Username englishNameObj = new Username(englishName);
        // when
        UserDomainException userDomainException1 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(userId, emailObj, phoneNumberObj, userObj, passwordObj));
        // then
        Assertions.assertNotNull(userDomainException1);
        Assertions.assertNotNull(userDomainException1.getMessage());
        Assertions.assertEquals("Invalid email", userDomainException1.getMessage());
        // when
        UserDomainException userDomainException2 = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.createUser(userId, emailObj2, phoneNumberObj, englishNameObj, passwordObj));
        // then
        Assertions.assertNotNull(userDomainException2);
        Assertions.assertNotNull(userDomainException2.getMessage());
        Assertions.assertEquals("Invalid username", userDomainException2.getMessage());
    }

    @Test
    @DisplayName("패스워드 리셋 로직 테스트")
    public void resetPasswordTest() {
        // given
        String newPassword = "newpassword";
        Password newPasswordObj = new Password(newPassword);
        // when 비밀번호 같은지 안같은지 확인하는 로직은 도메인 수준에서는 하지 않기로(Bcrypt가 사용되어야 함)
        userDomainService.updatePassword(mockUser, newPasswordObj);
        // then
    }

    @Test
    @DisplayName("프로필 이미지 변경 로직 테스트")
    public void changePasswordTest() {
        // given
        ProfileImage profileImage = new ProfileImage(newProfileImageId, "newImage","");
        // when
        userDomainService.updateProfileImage(mockUser3ByUserStaticLogic, profileImage);
        // then
        Assertions.assertEquals(mockUser3ByUserStaticLogic.getProfileImage(), profileImage);
        Assertions.assertEquals("newImage", mockUser3ByUserStaticLogic.getProfileImage().
                getProfileImageExtensionWithName());
    }

    @Test
    @DisplayName("유저에게 권한 부여 테스트")
    public void grantRoleTest() {
        // given
        RoleType roleType = RoleType.ADMIN;
        // when
        userDomainService.grantRole(mockUser3ByUserStaticLogic, roleType);
        // then
        // 이제 이놈권한 두개임
        Assertions.assertEquals(RoleType.ADMIN, mockUser3ByUserStaticLogic.getRoles().stream()
                .filter(r -> r.getRoleType().equals(roleType)).findFirst().get().getRoleType());
        Assertions.assertEquals(RoleType.USER, mockUser3ByUserStaticLogic.getRoles().stream()
                .filter(r -> r.getRoleType().equals(RoleType.USER)).findFirst().get().getRoleType());

        // given 유저가 중복된 권한을 받으려고 할 때
        userDomainService.deleteRole(mockUser3ByUserStaticLogic, roleType);
        RoleType roleType2 = RoleType.USER;
        UserDomainException userDomainException = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.grantRole(mockUser3ByUserStaticLogic, roleType2));
        Assertions.assertNotNull(userDomainException);
        Assertions.assertNotNull(userDomainException.getMessage());
        Assertions.assertEquals(String.format("%s is already granted to this user", roleType2), userDomainException.getMessage());
    }

    @Test
    @DisplayName("유저 2FA 인증 인가 및 인증 타입 설정")
    public void changeTwoFactorAuthMethodTest() {
        // given
        // when
        // 인가를 허용하지 않은 경우 에러 발생
        UserDomainException userDomainException = Assertions.assertThrows(UserDomainException.class,
                () -> userDomainService.enable2FASecurity(mockUser3ByUserStaticLogic));
        // then
        Assertions.assertNotNull(userDomainException);
        Assertions.assertNotNull(userDomainException.getMessage());
        Assertions.assertEquals("Two-factor authentication method is not set", userDomainException.getMessage());
        // given
        // 인가를 허용하고 인증 방식을 부여
        TwoFactorAuthMethod twoFactorAuthMethod2 = TwoFactorAuthMethod.EMAIL;
        userDomainService.userSelect2FASecurityMethod(mockUser3ByUserStaticLogic, twoFactorAuthMethod2);
        // when
        userDomainService.enable2FASecurity(mockUser3ByUserStaticLogic);
        // then
        Assertions.assertEquals(TwoFactorAuthMethod.EMAIL, mockUser3ByUserStaticLogic.getSecuritySettings().getTwoFactorAuthMethod());

        // 인증 방식 취소
        // when
        userDomainService.disable2FASecurity(mockUser3ByUserStaticLogic);
        // then

        Assertions.assertEquals(mockUser3ByUserStaticLogic.getSecuritySettings().getTwoFactorAuthMethod(), null);
        Assertions.assertEquals(mockUser3ByUserStaticLogic.getSecuritySettings().getIsEnabled(), false);
    }
}
