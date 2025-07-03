package shop.shportfolio.trading.application.exception;

public class MarketPausedException extends TradingApplicationException {

    public MarketPausedException(String message) {
        super(message);
    }

    public MarketPausedException(String message, Throwable cause) {
        super(message, cause);
    }
}
