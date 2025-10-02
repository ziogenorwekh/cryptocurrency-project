package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderEntity is a Querydsl query type for OrderEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderEntity extends EntityPathBase<OrderEntity> {

    private static final long serialVersionUID = -1047239903L;

    public static final QOrderEntity orderEntity = new QOrderEntity("orderEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath marketId = createString("marketId");

    public final StringPath orderId = createString("orderId");

    public final StringPath orderSide = createString("orderSide");

    public final EnumPath<shop.shportfolio.trading.domain.valueobject.OrderStatus> orderStatus = createEnum("orderStatus", shop.shportfolio.trading.domain.valueobject.OrderStatus.class);

    public final EnumPath<shop.shportfolio.trading.domain.valueobject.OrderType> orderType = createEnum("orderType", shop.shportfolio.trading.domain.valueobject.OrderType.class);

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> quantity = createNumber("quantity", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> remainingQuantity = createNumber("remainingQuantity", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QOrderEntity(String variable) {
        super(OrderEntity.class, forVariable(variable));
    }

    public QOrderEntity(Path<? extends OrderEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderEntity(PathMetadata metadata) {
        super(OrderEntity.class, metadata);
    }

}

