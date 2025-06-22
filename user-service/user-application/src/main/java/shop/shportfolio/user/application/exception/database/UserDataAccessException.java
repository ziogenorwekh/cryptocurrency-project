package shop.shportfolio.user.application.exception.database;

public class UserDataAccessException extends RuntimeException {
    public UserDataAccessException(String message) {
        super(message);
    }
}
