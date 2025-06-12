package shop.shportfolio.user.application.exception;

public class UserNotAuthenticationTemporaryEmailException extends UserApplicationException {
    public UserNotAuthenticationTemporaryEmailException(String message) {
        super(message);
    }
}
