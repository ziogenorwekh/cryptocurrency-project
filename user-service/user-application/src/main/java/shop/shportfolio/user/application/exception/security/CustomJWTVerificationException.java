package shop.shportfolio.user.application.exception.security;

public class CustomJWTVerificationException extends RuntimeException {

    public CustomJWTVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
