package shop.shportfolio.trading.application.handler.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;

@Component
public class TradingUpdateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingDomainService tradingDomainService;

    @Autowired
    public TradingUpdateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                TradingDomainService tradingDomainService) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingDomainService = tradingDomainService;
    }
}
