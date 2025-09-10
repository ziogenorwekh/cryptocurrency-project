package shop.shportfolio.portfolio.application.exception;

public class DataApiMapperException extends PortfolioApplicationException{
    public DataApiMapperException(String message) {
        super(message);
    }

    public DataApiMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
