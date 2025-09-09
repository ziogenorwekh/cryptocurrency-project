package shop.shportfolio.user.application.exception.security;

import shop.shportfolio.user.application.exception.UserApplicationException;

public class CustomJwtException extends UserApplicationException {
    public CustomJwtException(String message) {
        super(message);
    }

    public CustomJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
