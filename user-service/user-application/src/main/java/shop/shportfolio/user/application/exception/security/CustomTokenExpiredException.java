package shop.shportfolio.user.application.exception.security;

import java.time.Instant;

public class CustomTokenExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Instant expirationTime;

    public CustomTokenExpiredException(String message, Instant expiredOn) {
        super(message);
        this.expirationTime=expiredOn;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }
}
