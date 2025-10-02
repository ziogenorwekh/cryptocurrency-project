package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservationOrderEntity is a Querydsl query type for ReservationOrderEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationOrderEntity extends EntityPathBase<ReservationOrderEntity> {

    private static final long serialVersionUID = 408625429L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservationOrderEntity reservationOrderEntity = new QReservationOrderEntity("reservationOrderEntity");

    public final QOrderEntity _super = new QOrderEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> expireAt = createDateTime("expireAt", java.time.LocalDateTime.class);

    public final BooleanPath isRepeatable = createBoolean("isRepeatable");

    public final shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.QJpaTriggerCondition jpaTriggerCondition;

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

    public final DateTimePath<java.time.LocalDateTime> scheduledTime = createDateTime("scheduledTime", java.time.LocalDateTime.class);

    //inherited
    public final ComparablePath<java.util.UUID> userId = _super.userId;

    public QReservationOrderEntity(String variable) {
        this(ReservationOrderEntity.class, forVariable(variable), INITS);
    }

    public QReservationOrderEntity(Path<? extends ReservationOrderEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservationOrderEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservationOrderEntity(PathMetadata metadata, PathInits inits) {
        this(ReservationOrderEntity.class, metadata, inits);
    }

    public QReservationOrderEntity(Class<? extends ReservationOrderEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jpaTriggerCondition = inits.isInitialized("jpaTriggerCondition") ? new shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.QJpaTriggerCondition(forProperty("jpaTriggerCondition")) : null;
    }

}

