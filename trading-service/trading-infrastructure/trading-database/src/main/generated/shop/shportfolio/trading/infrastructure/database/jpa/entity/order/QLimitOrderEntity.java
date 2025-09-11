package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLimitOrderEntity is a Querydsl query type for LimitOrderEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLimitOrderEntity extends EntityPathBase<LimitOrderEntity> {

    private static final long serialVersionUID = 836084166L;

    public static final QLimitOrderEntity limitOrderEntity = new QLimitOrderEntity("limitOrderEntity");

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

    //inherited
    public final NumberPath<java.math.BigDecimal> remainingQuantity = _super.remainingQuantity;

    //inherited
    public final ComparablePath<java.util.UUID> userId = _super.userId;

    public QLimitOrderEntity(String variable) {
        super(LimitOrderEntity.class, forVariable(variable));
    }

    public QLimitOrderEntity(Path<? extends LimitOrderEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLimitOrderEntity(PathMetadata metadata) {
        super(LimitOrderEntity.class, metadata);
    }

}

