package application.test;


import application.tmpbean.TestMockBean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import shop.shportfolio.common.domain.valueobject.AuthCodeType;
import shop.shportfolio.common.domain.valueobject.Email;
import shop.shportfolio.common.domain.valueobject.PhoneNumber;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.application.UserApplicationService;
import shop.shportfolio.user.application.command.create.UserCreateCommand;
import shop.shportfolio.user.application.command.create.UserCreatedResponse;
import shop.shportfolio.user.application.command.track.TrackUserQueryResponse;
import shop.shportfolio.user.application.command.track.UserTrackQuery;
import shop.shportfolio.user.application.exception.UserNotAuthenticationTemporaryEmailException;
import shop.shportfolio.user.application.ports.output.cache.CacheAdapter;
import shop.shportfolio.user.application.ports.output.repository.UserRepositoryAdapter;
import shop.shportfolio.user.domain.UserDomainServiceImpl;
import shop.shportfolio.user.domain.entity.User;
import shop.shportfolio.user.domain.valueobject.Password;
import shop.shportfolio.user.domain.valueobject.Username;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TestMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceTest {

    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String username = "김철수";
    private final String phoneNumber = "01012345678";
    private final String email = "test@example.com";
    private final String password = "testpwd";
    private final UUID userId = UUID.randomUUID();
    User testUser = User.createUser(new UserId(userId), new Email(email),
            new PhoneNumber(phoneNumber), new Username(username), new Password(password));
    @Autowired
    private CacheAdapter cacheAdapter;

    @BeforeEach
    public void beforeEach() {

    }

    @Test
    @DisplayName("회원가입 테스트 && 이메일 임시 인증이 안된 상태에서 회원가입 시도 테스트")
    public void createUser() {
        // given
        UserCreateCommand userCreateCommand = new UserCreateCommand(userId, username, phoneNumber, email, password);
        // when
        UserNotAuthenticationTemporaryEmailException userNotAuthenticationTemporaryEmailException =
                Assertions.assertThrows(UserNotAuthenticationTemporaryEmailException.class, () -> {
                    userApplicationService.createUser(userCreateCommand);
                });
        // then
        Mockito.verify(cacheAdapter, Mockito.times(1)).isAuthenticatedUserId(Mockito.any());
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException);
        Assertions.assertNotNull(userNotAuthenticationTemporaryEmailException.getMessage());
        Assertions.assertEquals(userNotAuthenticationTemporaryEmailException.getMessage(),
                String.format("User %s has expired email authentication", userCreateCommand.getEmail()));

        // given
        UserCreateCommand userCreateCommand2 = new UserCreateCommand(userId, username, phoneNumber, email, password);
        UserCreatedResponse userCreatedResponse = new UserCreatedResponse(testUser.getId().getValue().toString(),
                testUser.getUsername().getValue(), testUser.getPhoneNumber().getValue(), testUser.getEmail().getValue(),
                testUser.getCreatedAt().getValue(), testUser.getRoles().stream().map(role -> role.getRoleType().toString()).toList(),
                testUser.getSecuritySettings().getIsEnabled(), "");
        Mockito.when(cacheAdapter.isAuthenticatedUserId(Mockito.any())).thenReturn(true);
        Mockito.when(userRepositoryAdapter.save(Mockito.any())).thenReturn(testUser);
        Mockito.doReturn(testUser).when(userRepositoryAdapter).save(Mockito.any());

        // when
        UserCreatedResponse createdResponse = userApplicationService.createUser(userCreateCommand2);


        // then
        Mockito.verify(userRepositoryAdapter, Mockito.times(1)).save(Mockito.any(User.class));
        Assertions.assertNotNull(createdResponse);
        Assertions.assertNotNull(createdResponse.getUserId());
        Assertions.assertNotNull(createdResponse.getEmail());
        Assertions.assertNotNull(createdResponse.getPhoneNumber());
        Assertions.assertEquals(1, createdResponse.getRoles().size());
        Assertions.assertNotNull(createdResponse.getCreatedAt());
        Assertions.assertEquals(false, createdResponse.getIs2FAEnabled());
        Assertions.assertEquals("", createdResponse.getTwoFactorAuthMethod());
    }

    @Test
    @DisplayName("유저 단건 조회 테스트")
    public void findOneUser() {
        // given
        UserTrackQuery userTrackQuery = new UserTrackQuery(userId);
        Mockito.when(userRepositoryAdapter.findByUserId(userTrackQuery.getUserId())).thenReturn(Optional.of(testUser));
        // when

        TrackUserQueryResponse queryResponse = userApplicationService.trackUserQuery(userTrackQuery);

        // then
        Mockito.verify(userRepositoryAdapter,Mockito.times(1)).findByUserId(userId);
        Assertions.assertNotNull(queryResponse);
        Assertions.assertEquals(userId.toString(), queryResponse.getUserId());
        Assertions.assertEquals(email, queryResponse.getEmail());
        Assertions.assertEquals(username, queryResponse.getUsername());
        Assertions.assertEquals(phoneNumber, queryResponse.getPhoneNumber());
        Assertions.assertEquals(1, queryResponse.getRoles().size());
        Assertions.assertNotNull(queryResponse.getCreatedAt());
        Assertions.assertEquals(false, queryResponse.getIs2FAEnabled());
        Assertions.assertEquals("",queryResponse.getTwoFactorAuthMethod());
    }
}
