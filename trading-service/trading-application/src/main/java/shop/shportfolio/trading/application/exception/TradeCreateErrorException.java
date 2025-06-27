package shop.shportfolio.trading.application.exception;

public class TradeCreateErrorException extends TradingApplicationException {
    public TradeCreateErrorException(String message) {
        super(message);
    }

    public TradeCreateErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
