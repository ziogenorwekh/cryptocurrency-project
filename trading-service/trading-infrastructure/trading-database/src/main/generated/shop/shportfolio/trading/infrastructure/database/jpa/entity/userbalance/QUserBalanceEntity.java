package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserBalanceEntity is a Querydsl query type for UserBalanceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBalanceEntity extends EntityPathBase<UserBalanceEntity> {

    private static final long serialVersionUID = 24945863L;

    public static final QUserBalanceEntity userBalanceEntity = new QUserBalanceEntity("userBalanceEntity");

    public final EnumPath<shop.shportfolio.common.domain.valueobject.AssetCode> assetCode = createEnum("assetCode", shop.shportfolio.common.domain.valueobject.AssetCode.class);

    public final ListPath<LockBalanceEntity, QLockBalanceEntity> lockBalances = this.<LockBalanceEntity, QLockBalanceEntity>createList("lockBalances", LockBalanceEntity.class, QLockBalanceEntity.class, PathInits.DIRECT2);

    public final NumberPath<java.math.BigDecimal> money = createNumber("money", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> userBalanceId = createComparable("userBalanceId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QUserBalanceEntity(String variable) {
        super(UserBalanceEntity.class, forVariable(variable));
    }

    public QUserBalanceEntity(Path<? extends UserBalanceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserBalanceEntity(PathMetadata metadata) {
        super(UserBalanceEntity.class, metadata);
    }

}

