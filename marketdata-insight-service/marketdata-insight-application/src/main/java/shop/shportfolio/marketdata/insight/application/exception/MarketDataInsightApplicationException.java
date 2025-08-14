package shop.shportfolio.marketdata.insight.application.exception;

public class MarketDataInsightApplicationException extends RuntimeException {
    public MarketDataInsightApplicationException(String message) {
      super(message);
    }

    public MarketDataInsightApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
