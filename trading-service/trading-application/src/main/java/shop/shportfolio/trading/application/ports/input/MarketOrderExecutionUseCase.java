package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.domain.entity.MarketOrder;

public interface MarketOrderExecutionUseCase {

    void executeMarketOrder(MarketOrder marketOrder);
}
