package shop.shportfolio.portfolio.application.exception;

public class PortfolioNotFoundException extends PortfolioApplicationException{
    public PortfolioNotFoundException(String message) {
        super(message);
    }

    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
