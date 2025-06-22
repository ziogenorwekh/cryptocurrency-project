package shop.shportfolio.user.application.exception.security;

public class TokenRequestTypeException extends RuntimeException {
    public TokenRequestTypeException(String message) {
        super(message);
    }
}
