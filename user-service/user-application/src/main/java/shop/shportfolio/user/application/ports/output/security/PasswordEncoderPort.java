package shop.shportfolio.user.application.ports.output.security;

public interface PasswordEncoderPort {

    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
