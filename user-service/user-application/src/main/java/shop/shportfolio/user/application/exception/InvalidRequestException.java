package shop.shportfolio.user.application.exception;

public class InvalidRequestException extends UserApplicationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
