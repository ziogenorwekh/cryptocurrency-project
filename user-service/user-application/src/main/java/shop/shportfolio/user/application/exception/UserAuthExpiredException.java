package shop.shportfolio.user.application.exception;

public class UserAuthExpiredException extends UserApplicationException{
    public UserAuthExpiredException(String message) {
        super(message);
    }
}
