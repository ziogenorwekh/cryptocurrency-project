package shop.shportfolio.user.api.test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.user.api.resources.UserLoginResources;
import shop.shportfolio.user.application.command.auth.LoginCommand;
import shop.shportfolio.user.application.command.auth.LoginResponse;
import shop.shportfolio.user.application.command.auth.LoginTwoFactorCommand;
import shop.shportfolio.user.application.ports.input.UserAuthenticationService;

import java.util.UUID;

public class UserLoginResourcesUnitTest {

    private UserAuthenticationService userAuthenticationService;
    private UserLoginResources userLoginResources;

    private final UUID userId = UUID.randomUUID();
    private final String accessToken = "accessToken";
    private final String tempToken = "tempToken";

    @BeforeEach
    void setUp() {
        userAuthenticationService = Mockito.mock(UserAuthenticationService.class);
        userLoginResources = new UserLoginResources(userAuthenticationService);
    }

    @Test
    @DisplayName("로그인 - 정상 호출")
    void login_Success() {
        LoginCommand loginCommand = new LoginCommand();
        LoginResponse mockResponse = new LoginResponse(userId,"test@example.com",accessToken,"COMPLETE");

        Mockito.when(userAuthenticationService.userLogin(Mockito.any(LoginCommand.class)))
                .thenReturn(mockResponse);

        ResponseEntity<LoginResponse> response = userLoginResources.login(loginCommand);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(mockResponse, response.getBody());
        Mockito.verify(userAuthenticationService).userLogin(Mockito.any(LoginCommand.class));
    }

    @Test
    @DisplayName("2FA 로그인 - 정상 호출")
    void login2FA_Success() {
        LoginTwoFactorCommand loginTwoFactorCommand = new LoginTwoFactorCommand();
        LoginResponse mockResponse = new LoginResponse(userId,"test@example.come",tempToken,"REQUIRE_2FA");

        Mockito.when(userAuthenticationService.userVerify2FACode(Mockito.any(LoginTwoFactorCommand.class)))
                .thenReturn(mockResponse);

        ResponseEntity<LoginResponse> response = userLoginResources.login2FA(loginTwoFactorCommand);

        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(mockResponse, response.getBody());
        Mockito.verify(userAuthenticationService).userVerify2FACode(Mockito.any(LoginTwoFactorCommand.class));
    }
}
