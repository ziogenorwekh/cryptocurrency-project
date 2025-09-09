package shop.shportfolio.user.application.exception.security;

import shop.shportfolio.user.application.exception.UserApplicationException;

public class CustomBadCredentialsException extends UserApplicationException {
    public CustomBadCredentialsException(String message) {
        super(message);
    }

    public CustomBadCredentialsException(String message, Throwable cause) {
        super(message,cause);
    }
}
