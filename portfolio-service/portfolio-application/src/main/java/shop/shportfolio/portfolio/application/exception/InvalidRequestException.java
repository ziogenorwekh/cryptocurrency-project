package shop.shportfolio.portfolio.application.exception;

public class InvalidRequestException extends PortfolioApplicationException{
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
