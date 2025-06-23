package shop.shportfolio.trading.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;

@Repository
public interface TradingRepositoryAdapter {

    LimitOrder saveLimitOrder(LimitOrder limitOrder);

    MarketOrder saveMarketOrder(MarketOrder marketOrder);

    void saveMarketItem(MarketItem marketItem);
}
