package shop.shportfolio.trading.application.handler.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;

@Component
public class TradingUpdateHandler {

    private final TradingRepositoryPort tradingRepositoryPort;
    private final TradingDomainService tradingDomainService;

    @Autowired
    public TradingUpdateHandler(TradingRepositoryPort tradingRepositoryPort,
                                TradingDomainService tradingDomainService) {
        this.tradingRepositoryPort = tradingRepositoryPort;
        this.tradingDomainService = tradingDomainService;
    }
}
