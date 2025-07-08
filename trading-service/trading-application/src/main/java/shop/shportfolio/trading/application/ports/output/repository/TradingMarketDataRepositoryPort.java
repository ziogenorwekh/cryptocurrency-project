package shop.shportfolio.trading.application.ports.output.repository;


import shop.shportfolio.trading.domain.entity.MarketItem;

import java.util.Optional;

public interface TradingMarketDataRepositoryPort {
    void saveMarketItem(MarketItem marketItem);
    Optional<MarketItem> findMarketItemByMarketId(String marketId);
}
