package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.marketdata.insight.application.exception.MarketItemNotFoundException;
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
import java.time.OffsetDateTime;
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
        MarketItemEntity marketItemEntity = marketItemJpaRepository
                .findMarketItemEntityByMarketId(aiAnalysisResult.getMarketId().getValue())
                .orElseThrow(()->new MarketItemNotFoundException(
                        String.format("Market Item with id %s not found", aiAnalysisResult.getMarketId().getValue())
                ));
        AIAnalysisResultEntity aiAnalysisResultEntity = mapper
                .aiAnalysisResultToAIAnalysisResultEntity(aiAnalysisResult, marketItemEntity);
        AIAnalysisResultEntity saved = aiAnalysisResultJpaRepository.save(aiAnalysisResultEntity);
        return mapper.aiAnalysisResultEntityToAIAnalysisResult(saved);
    }

    @Override
    public Optional<AIAnalysisResult> findAIAnalysisResult(String marketId, String periodType,
                                                           OffsetDateTime periodStart,
                                                           OffsetDateTime periodEnd) {
        QAIAnalysisResultEntity ai = QAIAnalysisResultEntity.aIAnalysisResultEntity;
        QMarketItemEntity market = QMarketItemEntity.marketItemEntity;

        AIAnalysisResultEntity entity = jpaQueryFactory
                .selectFrom(ai)
                .join(ai.marketItemEntity, market)
                .where(
                        market.marketId.eq(marketId),
                        ai.periodType.eq(PeriodType.valueOf(periodType)),
                        ai.periodStart.goe(periodStart),
                        ai.periodEnd.loe(periodEnd)
                )
                .fetchOne();
        return Optional.ofNullable(entity)
                .map(mapper::aiAnalysisResultEntityToAIAnalysisResult);
    }

    @Override
    public Optional<AIAnalysisResult> findLastAnalysis(String marketId, String periodType) {
        QAIAnalysisResultEntity ai = QAIAnalysisResultEntity.aIAnalysisResultEntity;
        QMarketItemEntity market = QMarketItemEntity.marketItemEntity;

        // periodType enum으로 변환
        PeriodType pt = PeriodType.valueOf(periodType);

        // 최신 분석 결과 가져오기
        AIAnalysisResultEntity entity = jpaQueryFactory
                .selectFrom(ai)
                .join(ai.marketItemEntity, market)
                .where(
                        market.marketId.eq(marketId),
                        ai.periodType.eq(pt)
                )
                .orderBy(ai.analysisTime.desc()) // 최신 시간 기준 정렬
                .fetchFirst(); // 가장 첫번째 = 최신

        return Optional.ofNullable(entity)
                .map(mapper::aiAnalysisResultEntityToAIAnalysisResult);
    }

}
