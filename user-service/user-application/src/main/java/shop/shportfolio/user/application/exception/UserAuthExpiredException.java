package shop.shportfolio.user.application.exception;

// 401
public class UserAuthExpiredException extends UserApplicationException{
    public UserAuthExpiredException(String message) {
        super(message);
    }
}
