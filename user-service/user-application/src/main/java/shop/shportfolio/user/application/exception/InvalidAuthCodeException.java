package shop.shportfolio.user.application.exception;

public class InvalidAuthCodeException extends UserApplicationException {
    public InvalidAuthCodeException(String message) {
        super(message);
    }
}
