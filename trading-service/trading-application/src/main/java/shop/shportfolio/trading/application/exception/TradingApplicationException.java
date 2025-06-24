package shop.shportfolio.trading.application.exception;

public class TradingApplicationException extends RuntimeException {

    public TradingApplicationException(String message) {
        super(message);
    }
    public TradingApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
