package shop.shportfolio.common.exception;

public class UserNotAccessException extends RuntimeException {

    public UserNotAccessException(String message) {
        super(message);
    }
}
