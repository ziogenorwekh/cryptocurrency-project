package shop.shportfolio.common.domain.exception;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
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
