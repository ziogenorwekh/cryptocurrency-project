package shop.shportfolio.user.application.ports.input;

import java.util.UUID;

public interface UserRegistrationUseCase {
    void isAuthenticatedTempUser(UUID userId, String email);
    UUID verifyTempEmailCodeAndCreateUserId(String email, String code);
    String sendTempEmailCodeForCreateUser(String email);
}
