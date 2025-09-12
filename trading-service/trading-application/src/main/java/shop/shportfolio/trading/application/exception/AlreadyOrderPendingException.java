package shop.shportfolio.trading.application.exception;

public class AlreadyOrderPendingException extends TradingApplicationException {
    public AlreadyOrderPendingException(String message) {
        super(message);
    }
}
