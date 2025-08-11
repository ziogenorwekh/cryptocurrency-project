package shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJpaTriggerCondition is a Querydsl query type for JpaTriggerCondition
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QJpaTriggerCondition extends BeanPath<JpaTriggerCondition> {

    private static final long serialVersionUID = 262755403L;

    public static final QJpaTriggerCondition jpaTriggerCondition = new QJpaTriggerCondition("jpaTriggerCondition");

    public final NumberPath<java.math.BigDecimal> targetPrice = createNumber("targetPrice", java.math.BigDecimal.class);

    public final EnumPath<shop.shportfolio.trading.domain.valueobject.TriggerType> triggerType = createEnum("triggerType", shop.shportfolio.trading.domain.valueobject.TriggerType.class);

    public QJpaTriggerCondition(String variable) {
        super(JpaTriggerCondition.class, forVariable(variable));
    }

    public QJpaTriggerCondition(Path<? extends JpaTriggerCondition> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJpaTriggerCondition(PathMetadata metadata) {
        super(JpaTriggerCondition.class, metadata);
    }

}

