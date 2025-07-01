package shop.shportfolio.trading.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TradingRepositoryAdapter {

    Optional<LimitOrder> findLimitOrderByOrderId(String orderId);
    Optional<LimitOrder> findLimitOrderByUserId(UUID userId);
    LimitOrder saveLimitOrder(LimitOrder limitOrder);

    Optional<MarketOrder> findMarketOrderByUserId(UUID userId);
    Optional<MarketOrder> findMarketOrderByOrderId(String orderId);
    MarketOrder saveMarketOrder(MarketOrder marketOrder);
    // 일주일마다 저장
    void saveMarketItem(MarketItem marketItem);

    Optional<MarketItem> findMarketItemByMarketId(String marketId);

    List<Trade> findTradesByMarketId(String marketId);

    void saveTrade(Trade trade);
}
