package shop.shportfolio.trading.application.exception;

public class OrderNotFoundException extends TradingApplicationException{
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
