package shop.shportfolio.user.application.ports.output.security;

import shop.shportfolio.user.domain.entity.Role;

import java.util.List;
import java.util.UUID;

public interface AuthenticatorPort {

    UUID authenticate(String email, String password);
    String generateLoginToken(UUID userId, List<Role> roles);
    String generate2FATmpToken(String email);

    String getEmailBy2FATmpToken(String token);
}
