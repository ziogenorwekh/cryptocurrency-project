package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.market.MarketItemEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.market.QMarketItemEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingMarketDataDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.MarketItemJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TradingMarketDataRepositoryAdapter implements TradingMarketDataRepositoryPort {

    private final TradingMarketDataDataAccessMapper mapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final MarketItemJpaRepository marketItemJpaRepository;

    @Autowired
    public TradingMarketDataRepositoryAdapter(TradingMarketDataDataAccessMapper mapper,
                                              JPAQueryFactory jpaQueryFactory,
                                              MarketItemJpaRepository marketItemJpaRepository) {
        this.mapper = mapper;
        this.jpaQueryFactory = jpaQueryFactory;
        this.marketItemJpaRepository = marketItemJpaRepository;
    }

    @Override
    public void saveMarketItem(MarketItem marketItem) {
        MarketItemEntity marketItemEntity = mapper.marketItemToMarketItemEntity(marketItem);
        marketItemJpaRepository.save(marketItemEntity);
    }

    @Override
    public Optional<MarketItem> findMarketItemByMarketId(String marketId) {
        QMarketItemEntity marketItemEntity = QMarketItemEntity.marketItemEntity;
        MarketItemEntity fetchedOne = jpaQueryFactory.selectFrom(marketItemEntity)
                .where(marketItemEntity.marketId.eq(marketId))
                .fetchOne();

        if (fetchedOne == null) {
            return Optional.empty();
        }

        return Optional.of(mapper.marketItemEntityToMarketItem(fetchedOne));
    }

    @Override
    public List<MarketItem> findAllMarketItems() {
        QMarketItemEntity marketItemEntity = QMarketItemEntity.marketItemEntity;
        List<MarketItemEntity> fetched = jpaQueryFactory.selectFrom(marketItemEntity).fetch();
        return fetched.stream().map(mapper::marketItemEntityToMarketItem)
                .collect(Collectors.toList());
    }
}
