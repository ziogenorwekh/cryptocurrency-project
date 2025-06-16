package shop.shportfolio.user.api.exception;

public class UserNotAccessException extends RuntimeException {

    public UserNotAccessException(String message) {
        super(message);
    }
}
