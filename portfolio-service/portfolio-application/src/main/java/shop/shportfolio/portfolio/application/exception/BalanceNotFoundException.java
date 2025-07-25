package shop.shportfolio.portfolio.application.exception;

public class BalanceNotFoundException extends PortfolioApplicationException {
    public BalanceNotFoundException(String message) {
        super(message);
    }

    public BalanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
