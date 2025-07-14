package shop.shportfolio.trading.application.exception;

public class OrderInValidatedException extends TradingApplicationException{
    public OrderInValidatedException(String message) {
        super(message);
    }
}
