package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.AIAnalysisResultEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.MarketItemEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.QAIAnalysisResultEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.QMarketItemEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.mapper.MarketDataInsightDataAccessMapper;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.repository.AIAnalysisResultJpaRepository;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.repository.MarketItemJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AIAnalysisResultRepositoryAdapter implements AIAnalysisResultRepositoryPort {

    private final MarketItemJpaRepository marketItemJpaRepository;
    private final AIAnalysisResultJpaRepository aiAnalysisResultJpaRepository;
    private final MarketDataInsightDataAccessMapper mapper;
    private final JPAQueryFactory jpaQueryFactory;
    @Autowired
    public AIAnalysisResultRepositoryAdapter(MarketItemJpaRepository marketItemJpaRepository,
                                             AIAnalysisResultJpaRepository aiAnalysisResultJpaRepository,
                                             MarketDataInsightDataAccessMapper mapper,
                                             JPAQueryFactory jpaQueryFactory) {
        this.marketItemJpaRepository = marketItemJpaRepository;
        this.aiAnalysisResultJpaRepository = aiAnalysisResultJpaRepository;
        this.mapper = mapper;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public AIAnalysisResult saveAIAnalysisResult(AIAnalysisResult aiAnalysisResult) {

        return null;
    }

    @Override
    public Optional<AIAnalysisResult> findAIAnalysisResult(String marketId, String periodType,
                                                           LocalDateTime periodStart,
                                                           LocalDateTime periodEnd) {
        QAIAnalysisResultEntity ai = QAIAnalysisResultEntity.aIAnalysisResultEntity;
        QMarketItemEntity market = QMarketItemEntity.marketItemEntity;

        AIAnalysisResultEntity result = jpaQueryFactory
                .select(ai)
                .from(ai)
                .join(ai.marketItemEntity, market)
                .where(
                        market.marketId.eq(marketId),
                        ai.periodType.eq(PeriodType.valueOf(periodType)),
                        ai.periodStart.goe(periodStart),
                        ai.periodEnd.loe(periodEnd)
                )
                .fetchFirst();
        return Optional.empty();
    }
}
