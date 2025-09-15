package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAIAnalysisResultEntity is a Querydsl query type for AIAnalysisResultEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAIAnalysisResultEntity extends EntityPathBase<AIAnalysisResultEntity> {

    private static final long serialVersionUID = 166957897L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAIAnalysisResultEntity aIAnalysisResultEntity = new QAIAnalysisResultEntity("aIAnalysisResultEntity");

    public final ComparablePath<java.util.UUID> aiAnalysisResultId = createComparable("aiAnalysisResultId", java.util.UUID.class);

    public final DateTimePath<java.time.OffsetDateTime> analysisTime = createDateTime("analysisTime", java.time.OffsetDateTime.class);

    public final QMarketItemEntity marketItemEntity;

    public final NumberPath<java.math.BigDecimal> momentumScore = createNumber("momentumScore", java.math.BigDecimal.class);

    public final DateTimePath<java.time.OffsetDateTime> periodEnd = createDateTime("periodEnd", java.time.OffsetDateTime.class);

    public final DateTimePath<java.time.OffsetDateTime> periodStart = createDateTime("periodStart", java.time.OffsetDateTime.class);

    public final EnumPath<shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType> periodType = createEnum("periodType", shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType.class);

    public final EnumPath<shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend> priceTrend = createEnum("priceTrend", shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend.class);

    public final EnumPath<shop.shportfolio.marketdata.insight.domain.valueobject.Signal> signal = createEnum("signal", shop.shportfolio.marketdata.insight.domain.valueobject.Signal.class);

    public final StringPath summaryCommentEng = createString("summaryCommentEng");

    public final StringPath summaryCommentKor = createString("summaryCommentKor");

    public QAIAnalysisResultEntity(String variable) {
        this(AIAnalysisResultEntity.class, forVariable(variable), INITS);
    }

    public QAIAnalysisResultEntity(Path<? extends AIAnalysisResultEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAIAnalysisResultEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAIAnalysisResultEntity(PathMetadata metadata, PathInits inits) {
        this(AIAnalysisResultEntity.class, metadata, inits);
    }

    public QAIAnalysisResultEntity(Class<? extends AIAnalysisResultEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.marketItemEntity = inits.isInitialized("marketItemEntity") ? new QMarketItemEntity(forProperty("marketItemEntity")) : null;
    }

}

