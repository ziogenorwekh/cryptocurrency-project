package shop.shportfolio.trading.application.exception;

public class UserBalanceNotFoundException extends TradingApplicationException {

    public UserBalanceNotFoundException(String message) {
        super(message);
    }
}
