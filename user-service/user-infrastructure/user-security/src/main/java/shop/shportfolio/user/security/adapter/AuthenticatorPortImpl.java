package shop.shportfolio.user.security.adapter;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.security.AuthenticatorPort;

import java.util.UUID;


@Component
public class AuthenticatorPortImpl implements AuthenticatorPort {

    @Override
    public UUID authenticate(String email, String password) {
        return null;
    }

    @Override
    public String generateAccessToken(UUID uuid) {
        return "";
    }

    @Override
    public String generate2FATempToken(String email) {
        return "";
    }

    @Override
    public String getEmailByTempToken(String token) {
        return "";
    }
}
