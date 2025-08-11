package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMarketOrderEntity is a Querydsl query type for MarketOrderEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketOrderEntity extends EntityPathBase<MarketOrderEntity> {

    private static final long serialVersionUID = 557563205L;

    public static final QMarketOrderEntity marketOrderEntity = new QMarketOrderEntity("marketOrderEntity");

    public final QOrderEntity _super = new QOrderEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath marketId = _super.marketId;

    //inherited
    public final StringPath orderId = _super.orderId;

    //inherited
    public final StringPath orderSide = _super.orderSide;

    //inherited
    public final EnumPath<shop.shportfolio.trading.domain.valueobject.OrderStatus> orderStatus = _super.orderStatus;

    //inherited
    public final EnumPath<shop.shportfolio.trading.domain.valueobject.OrderType> orderType = _super.orderType;

    //inherited
    public final NumberPath<java.math.BigDecimal> price = _super.price;

    //inherited
    public final NumberPath<java.math.BigDecimal> quantity = _super.quantity;

    public final NumberPath<java.math.BigDecimal> remainingPrice = createNumber("remainingPrice", java.math.BigDecimal.class);

    //inherited
    public final NumberPath<java.math.BigDecimal> remainingQuantity = _super.remainingQuantity;

    //inherited
    public final ComparablePath<java.util.UUID> userId = _super.userId;

    public QMarketOrderEntity(String variable) {
        super(MarketOrderEntity.class, forVariable(variable));
    }

    public QMarketOrderEntity(Path<? extends MarketOrderEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMarketOrderEntity(PathMetadata metadata) {
        super(MarketOrderEntity.class, metadata);
    }

}

