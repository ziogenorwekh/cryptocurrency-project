package shop.shportfolio.user.application.ports.output.security;

public interface PasswordEncoderAdapter {

    boolean matches(String rawPassword, String encodedPassword);
    String encode(String rawPassword);
}
