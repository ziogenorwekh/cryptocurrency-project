package shop.shportfolio.user.database.jpa.exception;

public class UserDataAccessException extends RuntimeException {
    public UserDataAccessException(String message) {
        super(message);
    }
}
