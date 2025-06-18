package shop.shportfolio.trading.domain.exception;

import shop.shportfolio.common.domain.exception.DomainException;

import java.io.Serial;

public class TradingDomainException extends DomainException {
    @Serial
    private static final long serialVersionUID = 1L;
    public TradingDomainException(String message) {
        super(message);
    }
}
