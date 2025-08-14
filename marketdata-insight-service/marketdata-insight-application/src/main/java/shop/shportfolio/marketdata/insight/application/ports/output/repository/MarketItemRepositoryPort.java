package shop.shportfolio.marketdata.insight.application.ports.output.repository;

import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

import java.util.List;
import java.util.Optional;

public interface MarketItemRepositoryPort {


    Optional<MarketItem> findMarketItemByMarketId(String marketId);
    List<MarketItem> findAllMarketItems();

    MarketItem saveMarketItem(MarketItem marketItem);
}
