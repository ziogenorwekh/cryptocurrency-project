package shop.shportfolio.user.application.exception;

public class UserApplicationException extends RuntimeException {
  public UserApplicationException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public synchronized Throwable getCause() {
    return super.getCause();
  }
}
