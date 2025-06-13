package shop.shportfolio.user.application.ports.output.cache;

import java.util.UUID;

public interface CacheAdapter {



    Boolean isAuthenticatedUserId(UUID userId);

    String saveTempEmailCode(String email);

    Boolean verifyTempEmailAuthCode(String email, String code);

    void deleteTempEmailCode(String email);

    String saveTempUserId(UUID userId, String email);
}
