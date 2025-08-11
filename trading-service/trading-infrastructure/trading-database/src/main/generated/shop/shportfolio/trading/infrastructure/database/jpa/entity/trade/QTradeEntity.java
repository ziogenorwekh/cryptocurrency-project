package shop.shportfolio.trading.infrastructure.database.jpa.entity.trade;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTradeEntity is a Querydsl query type for TradeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTradeEntity extends EntityPathBase<TradeEntity> {

    private static final long serialVersionUID = 1558601165L;

    public static final QTradeEntity tradeEntity = new QTradeEntity("tradeEntity");

    public final StringPath buyOrderId = createString("buyOrderId");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> feeAmount = createNumber("feeAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> feeRate = createNumber("feeRate", java.math.BigDecimal.class);

    public final StringPath marketId = createString("marketId");

    public final NumberPath<java.math.BigDecimal> orderPrice = createNumber("orderPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> quantity = createNumber("quantity", java.math.BigDecimal.class);

    public final StringPath sellOrderId = createString("sellOrderId");

    public final ComparablePath<java.util.UUID> tradeId = createComparable("tradeId", java.util.UUID.class);

    public final EnumPath<shop.shportfolio.common.domain.valueobject.TransactionType> transactionType = createEnum("transactionType", shop.shportfolio.common.domain.valueobject.TransactionType.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QTradeEntity(String variable) {
        super(TradeEntity.class, forVariable(variable));
    }

    public QTradeEntity(Path<? extends TradeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTradeEntity(PathMetadata metadata) {
        super(TradeEntity.class, metadata);
    }

}

