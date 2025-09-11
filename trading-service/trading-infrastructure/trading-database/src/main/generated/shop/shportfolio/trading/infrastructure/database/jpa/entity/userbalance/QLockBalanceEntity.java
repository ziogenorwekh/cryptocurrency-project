package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLockBalanceEntity is a Querydsl query type for LockBalanceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLockBalanceEntity extends EntityPathBase<LockBalanceEntity> {

    private static final long serialVersionUID = -998963353L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLockBalanceEntity lockBalanceEntity = new QLockBalanceEntity("lockBalanceEntity");

    public final NumberPath<java.math.BigDecimal> lockedAmount = createNumber("lockedAmount", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> lockedAt = createDateTime("lockedAt", java.time.LocalDateTime.class);

    public final EnumPath<shop.shportfolio.trading.domain.valueobject.LockStatus> lockStatus = createEnum("lockStatus", shop.shportfolio.trading.domain.valueobject.LockStatus.class);

    public final StringPath orderId = createString("orderId");

    public final QUserBalanceEntity userBalance;

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QLockBalanceEntity(String variable) {
        this(LockBalanceEntity.class, forVariable(variable), INITS);
    }

    public QLockBalanceEntity(Path<? extends LockBalanceEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLockBalanceEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLockBalanceEntity(PathMetadata metadata, PathInits inits) {
        this(LockBalanceEntity.class, metadata, inits);
    }

    public QLockBalanceEntity(Class<? extends LockBalanceEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userBalance = inits.isInitialized("userBalance") ? new QUserBalanceEntity(forProperty("userBalance")) : null;
    }

}

