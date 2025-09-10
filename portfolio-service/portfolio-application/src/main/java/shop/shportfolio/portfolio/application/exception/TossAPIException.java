package shop.shportfolio.portfolio.application.exception;

public class TossAPIException extends PortfolioApplicationException{
    public TossAPIException(String message) {
        super(message);
    }

    public TossAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
