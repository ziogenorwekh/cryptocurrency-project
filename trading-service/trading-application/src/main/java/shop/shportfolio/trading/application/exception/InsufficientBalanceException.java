package shop.shportfolio.trading.application.exception;

public class InsufficientBalanceException extends TradingApplicationException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
