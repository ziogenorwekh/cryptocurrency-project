package shop.shportfolio.trading.infrastructure.database.jpa.entity.market;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketItemEntity is a Querydsl query type for MarketItemEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketItemEntity extends EntityPathBase<MarketItemEntity> {

    private static final long serialVersionUID = -1600148780L;

    public static final QMarketItemEntity marketItemEntity = new QMarketItemEntity("marketItemEntity");

    public final StringPath marketEnglishName = createString("marketEnglishName");

    public final StringPath marketId = createString("marketId");

    public final StringPath marketKoreanName = createString("marketKoreanName");

    public final EnumPath<shop.shportfolio.common.domain.valueobject.MarketStatus> marketStatus = createEnum("marketStatus", shop.shportfolio.common.domain.valueobject.MarketStatus.class);

    public final StringPath marketWarning = createString("marketWarning");

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

