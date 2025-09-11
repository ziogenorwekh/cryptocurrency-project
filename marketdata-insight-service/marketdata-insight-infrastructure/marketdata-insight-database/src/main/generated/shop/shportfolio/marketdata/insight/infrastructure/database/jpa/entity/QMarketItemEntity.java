package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMarketItemEntity is a Querydsl query type for MarketItemEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketItemEntity extends EntityPathBase<MarketItemEntity> {

    private static final long serialVersionUID = -22906441L;

    public static final QMarketItemEntity marketItemEntity = new QMarketItemEntity("marketItemEntity");

    public final ListPath<AIAnalysisResultEntity, QAIAnalysisResultEntity> aiAnalysisResults = this.<AIAnalysisResultEntity, QAIAnalysisResultEntity>createList("aiAnalysisResults", AIAnalysisResultEntity.class, QAIAnalysisResultEntity.class, PathInits.DIRECT2);

    public final StringPath marketEnglishName = createString("marketEnglishName");

    public final StringPath marketId = createString("marketId");

    public final StringPath marketKoreanName = createString("marketKoreanName");

    public final EnumPath<shop.shportfolio.common.domain.valueobject.MarketStatus> marketStatus = createEnum("marketStatus", shop.shportfolio.common.domain.valueobject.MarketStatus.class);

    public QMarketItemEntity(String variable) {
        super(MarketItemEntity.class, forVariable(variable));
    }

    public QMarketItemEntity(Path<? extends MarketItemEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketItemEntity(PathMetadata metadata) {
        super(MarketItemEntity.class, metadata);
    }

}

