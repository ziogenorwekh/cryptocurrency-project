package shop.shportfolio.trading.application.exception;

public class MarketItemNotFoundException extends TradingApplicationException {
    public MarketItemNotFoundException(String message) {
        super(message);
    }

    public MarketItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
