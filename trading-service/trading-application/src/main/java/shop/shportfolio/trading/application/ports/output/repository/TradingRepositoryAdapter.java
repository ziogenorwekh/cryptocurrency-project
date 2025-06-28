package shop.shportfolio.trading.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradingRepositoryAdapter {

    LimitOrder saveLimitOrder(LimitOrder limitOrder);

    MarketOrder saveMarketOrder(MarketOrder marketOrder);

    // 일주일마다 저장
    void saveMarketItem(MarketItem marketItem);

    Optional<MarketItem> findMarketItemByMarketId(String marketId);

    List<Trade> findTradesByMarketId(String marketId);

    Trade saveTrade(Trade trade);
}
