package shop.shportfolio.portfolio.application.exception;

public class PortfolioExistException extends PortfolioApplicationException {
    public PortfolioExistException(String message) {
        super(message);
    }

    public PortfolioExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
