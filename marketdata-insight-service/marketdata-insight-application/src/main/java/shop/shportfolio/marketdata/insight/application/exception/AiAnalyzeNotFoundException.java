package shop.shportfolio.marketdata.insight.application.exception;

public class AiAnalyzeNotFoundException extends MarketDataInsightApplicationException{
    public AiAnalyzeNotFoundException(String message) {
        super(message);
    }

    public AiAnalyzeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
