package shop.shportfolio.user.application.ports.output.security;

import java.util.UUID;

public interface AuthenticatorPort {

    UUID authenticate(String email, String password);
    String generateLoginToken(UUID userId);
    String generate2FATmpToken(String email);

    String getEmailBy2FATmpToken(String token);
}
