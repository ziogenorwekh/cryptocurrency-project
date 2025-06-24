package shop.shportfolio.trading.application.exception;

public class OrderBookNotFoundException extends TradingApplicationException {
    public OrderBookNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
