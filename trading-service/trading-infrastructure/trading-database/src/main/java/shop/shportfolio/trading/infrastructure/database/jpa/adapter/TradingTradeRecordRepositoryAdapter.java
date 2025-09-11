package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.QTradeEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.TradeEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingTradeDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.TradeJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TradingTradeRecordRepositoryAdapter implements TradingTradeRecordRepositoryPort {

    private final TradeJpaRepository tradeJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final TradingTradeDataAccessMapper mapper;
    @Autowired
    public TradingTradeRecordRepositoryAdapter(TradeJpaRepository tradeJpaRepository,
                                               JPAQueryFactory jpaQueryFactory,
                                               TradingTradeDataAccessMapper mapper) {
        this.tradeJpaRepository = tradeJpaRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.mapper = mapper;
    }

    @Override
    public List<Trade> findTradesByMarketId(String marketId) {
        QTradeEntity tradeEntity = QTradeEntity.tradeEntity;
        List<TradeEntity> list = jpaQueryFactory.selectFrom(tradeEntity)
                .where(tradeEntity.marketId.eq(marketId)).fetch();
        return list.stream().map(mapper::tradeEntityToTrade).toList();
    }

    @Override
    public Trade saveTrade(Trade trade) {
        TradeEntity tradeEntity = mapper.tradeToTradeEntity(trade);
        return mapper.tradeEntityToTrade(tradeJpaRepository.save(tradeEntity));
    }

    @Override
    public Optional<Trade> findTopByMarketIdOrderByCreatedAtDesc(String marketId) {
        QTradeEntity tradeEntity = QTradeEntity.tradeEntity;
        TradeEntity result = jpaQueryFactory.selectFrom(tradeEntity)
                .where(tradeEntity.marketId.eq(marketId))
                .orderBy(tradeEntity.createdAt.desc())
                .limit(1)
                .fetchOne();
        if (result == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(mapper.tradeEntityToTrade(result));
    }

    @Override
    public List<Trade> findTradesByMarketIdAndCreatedAtBetween(String marketId, LocalDateTime from,
                                                               LocalDateTime to, Integer count) {
        QTradeEntity tradeEntity = QTradeEntity.tradeEntity;
        List<TradeEntity> list = jpaQueryFactory.selectFrom(tradeEntity)
                .where(tradeEntity.marketId.eq(marketId)
                        .and(tradeEntity.createdAt.between(from, to)))
                .orderBy(tradeEntity.createdAt.asc())
                .limit(count != null ? count : 100)
                .fetch();
        return list.stream().map(mapper::tradeEntityToTrade).toList();
    }
}
