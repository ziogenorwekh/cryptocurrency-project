package shop.shportfolio.user.application.ports.output.security;

import java.util.UUID;

public interface AuthenticatorPort {

    UUID authenticate(String email, String password);
    String generateAccessToken(UUID uuid);
    String generate2FATempToken(String email);

    String getEmailByTempToken(String token);
}
