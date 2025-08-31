package shop.shportfolio.matching.application.exception;

public class OrderBookNotFoundException extends MatchingApplicationException {
    public OrderBookNotFoundException(String message) {
        super(message);
    }
}
