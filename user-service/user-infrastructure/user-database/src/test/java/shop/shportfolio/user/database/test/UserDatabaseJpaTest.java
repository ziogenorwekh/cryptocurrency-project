package shop.shportfolio.user.database.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import shop.shportfolio.common.domain.valueobject.RoleType;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.infrastructure.database.jpa.adapter.UserRepositoryAdapterImpl;
import shop.shportfolio.user.application.exception.database.UserDataAccessException;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.*;

import java.util.Optional;
import java.util.UUID;

//@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = {JpaTestConfiguration.class})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2,
        replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserDatabaseJpaTest {

    @Autowired
    private UserRepositoryAdapterImpl userRepositoryAdapter;

    private UUID userId;
    private String username;
    private String password;
    private String email;
    private String phone;

    private User user;
    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        username = "englishName";
        password = "password";
        email = "test@example.com";
        phone = "01012345678";

        user = User.createUser(new UserId(userId), new Email(email),
                new PhoneNumber(phone), new Username(username), new Password(password));
    }

    @Test
    @DisplayName("유저 저장 되는지 테스트 && 매퍼가 잘 매핑했는지도 테스트")
    public void saveUser() {
        // given && when
        User saved = userRepositoryAdapter.save(user);

        // then
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getId());

        Assertions.assertNotNull(saved.getUsername());
        Assertions.assertNotNull(saved.getUsername().getValue());
        Assertions.assertFalse(saved.getUsername().getValue().isEmpty());

        Assertions.assertNotNull(saved.getEmail());
        Assertions.assertNotNull(saved.getEmail().getValue());
        Assertions.assertFalse(saved.getEmail().getValue().isEmpty());

        Assertions.assertNotNull(saved.getPhoneNumber());
        Assertions.assertNotNull(saved.getPhoneNumber().getValue());
        Assertions.assertFalse(saved.getPhoneNumber().getValue().isEmpty());

        Assertions.assertNotNull(saved.getPassword());
        Assertions.assertNotNull(saved.getPassword().getValue());
        Assertions.assertFalse(saved.getPassword().getValue().isEmpty());

        Assertions.assertNotNull(saved.getProfileImage());
        Assertions.assertNotNull(saved.getProfileImage().getValue());
        Assertions.assertFalse(saved.getProfileImage().getValue().toString().isEmpty());

        Assertions.assertNotNull(saved.getSecuritySettings());
        Assertions.assertNotNull(saved.getSecuritySettings().getId());
        Assertions.assertNotNull(saved.getSecuritySettings().getIsEnabled());

        Assertions.assertNotNull(saved.getRoles());
        Assertions.assertFalse(saved.getRoles().isEmpty());
        Assertions.assertNotNull(saved.getRoles().get(0));
        Assertions.assertNotNull(saved.getRoles().get(0).getRoleType());
        Assertions.assertEquals(RoleType.USER, saved.getRoles().get(0).getRoleType());
    }

    @Test
    @DisplayName("유저 아이디로 조회 테스트")
    public void findOneUser() {
        // given
        userRepositoryAdapter.save(user);
        Optional<User> optionalUser = userRepositoryAdapter.findByUserId(userId);
        // when
        Assertions.assertTrue(optionalUser.isPresent());
        // then
        User found = optionalUser.get();
        Assertions.assertEquals(userId, found.getId().getValue());
        Assertions.assertEquals(username, found.getUsername().getValue());
        Assertions.assertEquals(email, found.getEmail().getValue());
        Assertions.assertEquals(phone, found.getPhoneNumber().getValue());
    }

    @Test
    @DisplayName("이메일로 유저 조회 테스트")
    public void findByEmail() {
        // given
        userRepositoryAdapter.save(user);
        // when
        Optional<User> optionalUser = userRepositoryAdapter.findByEmail(email);
        // then
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(email, optionalUser.get().getEmail().getValue());
    }

    @Test
    @DisplayName("유저명으로 유저 조회 테스트")
    public void findByUsername() {
        // given
        userRepositoryAdapter.save(user);
        // when
        Optional<User> optionalUser = userRepositoryAdapter.findByUsername(username);
        // then
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(username, optionalUser.get().getUsername().getValue());
    }

    @Test
    @DisplayName("전화번호로 유저 조회 테스트")
    public void findByPhoneNumber() {
        // given
        userRepositoryAdapter.save(user);
        // when
        Optional<User> optionalUser = userRepositoryAdapter.findByPhoneNumber(phone);
        // then
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(phone, optionalUser.get().getPhoneNumber().getValue());
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    public void deleteUserById() {
        // given
        userRepositoryAdapter.save(user);

        // when
        userRepositoryAdapter.deleteUserById(userId);
        Optional<User> optionalUser = userRepositoryAdapter.findByUserId(userId);

        // then
        Assertions.assertFalse(optionalUser.isPresent());
    }

    @Test
    @DisplayName("없는 유저 삭제 시 예외 발생 테스트")
    public void deleteNonExistentUserThrowsException() {
        // given
        UUID notfoundId = UUID.randomUUID();

        // when
        UserDataAccessException userDataAccessException = Assertions.assertThrows(UserDataAccessException.class, () -> {
            userRepositoryAdapter.deleteUserById(notfoundId);
        });
        // then
        Assertions.assertNotNull(userDataAccessException);
        Assertions.assertEquals(String.format("User with id %s not found", notfoundId),
                userDataAccessException.getMessage());
    }
}
