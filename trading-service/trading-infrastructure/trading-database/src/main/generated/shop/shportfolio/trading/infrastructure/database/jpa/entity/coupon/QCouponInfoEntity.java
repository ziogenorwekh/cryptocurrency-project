package shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCouponInfoEntity is a Querydsl query type for CouponInfoEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponInfoEntity extends EntityPathBase<CouponInfoEntity> {

    private static final long serialVersionUID = -1085445905L;

    public static final QCouponInfoEntity couponInfoEntity = new QCouponInfoEntity("couponInfoEntity");

    public final ComparablePath<java.util.UUID> couponId = createComparable("couponId", java.util.UUID.class);

    public final NumberPath<Integer> feeDiscount = createNumber("feeDiscount", Integer.class);

    public final DatePath<java.time.LocalDate> issuedAt = createDate("issuedAt", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> usageExpiryDate = createDate("usageExpiryDate", java.time.LocalDate.class);

    public final ComparablePath<java.util.UUID> userId = createComparable("userId", java.util.UUID.class);

    public QCouponInfoEntity(String variable) {
        super(CouponInfoEntity.class, forVariable(variable));
    }

    public QCouponInfoEntity(Path<? extends CouponInfoEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCouponInfoEntity(PathMetadata metadata) {
        super(CouponInfoEntity.class, metadata);
    }

}

