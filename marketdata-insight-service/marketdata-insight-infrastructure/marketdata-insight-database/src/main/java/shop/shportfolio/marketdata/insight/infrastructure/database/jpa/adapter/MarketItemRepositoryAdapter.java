package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.MarketItemRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.MarketItemEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.mapper.MarketDataInsightDataAccessMapper;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.repository.MarketItemJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MarketItemRepositoryAdapter implements MarketItemRepositoryPort {

    private final MarketItemJpaRepository repository;
    private final MarketDataInsightDataAccessMapper mapper;
    @Autowired
    public MarketItemRepositoryAdapter(MarketItemJpaRepository repository,
                                       MarketDataInsightDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<MarketItem> findMarketItemByMarketId(String marketId) {
        return repository.findMarketItemEntityByMarketId(marketId).map(mapper::marketItemEntityToMarketItem);
    }

    @Override
    public List<MarketItem> findAllMarketItems() {
        return repository.findAll().stream().map(mapper::marketItemEntityToMarketItem).toList();
    }

    @Override
    public MarketItem saveMarketItem(MarketItem marketItem) {
        MarketItemEntity marketItemEntity = mapper.marketItemToMarketItemEntity(marketItem);
        MarketItemEntity saved = repository.save(marketItemEntity);
        return mapper.marketItemEntityToMarketItem(saved);
    }
}
