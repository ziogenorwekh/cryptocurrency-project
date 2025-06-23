package shop.shportfolio.user.application.exception;

public class InvalidPasswordException extends UserApplicationException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
