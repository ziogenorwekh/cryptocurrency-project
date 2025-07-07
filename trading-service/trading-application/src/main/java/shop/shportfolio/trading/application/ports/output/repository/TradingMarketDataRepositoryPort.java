package shop.shportfolio.trading.application.ports.output.repository;


import shop.shportfolio.trading.domain.entity.MarketItem;

import java.util.Optional;

public interface TradingMarketDataRepositoryPort {
    // 일주일마다 저장
    void saveMarketItem(MarketItem marketItem);
    Optional<MarketItem> findMarketItemByMarketId(String marketId);
}
