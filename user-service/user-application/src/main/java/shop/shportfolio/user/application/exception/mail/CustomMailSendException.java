package shop.shportfolio.user.application.exception.mail;

public class CustomMailSendException extends RuntimeException {
    public CustomMailSendException(String message, Throwable cause) {
        super(message, cause);
    }
    public CustomMailSendException(String message) {
        super(message);
    }
}
