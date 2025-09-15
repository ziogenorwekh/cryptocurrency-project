package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCryptoBalanceEntity is a Querydsl query type for CryptoBalanceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCryptoBalanceEntity extends EntityPathBase<CryptoBalanceEntity> {

    private static final long serialVersionUID = -832475439L;

    public static final QCryptoBalanceEntity cryptoBalanceEntity = new QCryptoBalanceEntity("cryptoBalanceEntity");

    public final StringPath balanceId = createString("balanceId");

    public final StringPath marketId = createString("marketId");

    public final NumberPath<java.math.BigDecimal> purchasePrice = createNumber("purchasePrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> quantity = createNumber("quantity", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QCryptoBalanceEntity(String variable) {
        super(CryptoBalanceEntity.class, forVariable(variable));
    }

    public QCryptoBalanceEntity(Path<? extends CryptoBalanceEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCryptoBalanceEntity(PathMetadata metadata) {
        super(CryptoBalanceEntity.class, metadata);
    }

}

