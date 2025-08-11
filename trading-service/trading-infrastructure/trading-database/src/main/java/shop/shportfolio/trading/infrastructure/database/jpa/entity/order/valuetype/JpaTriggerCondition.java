package shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.domain.valueobject.TriggerType;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class JpaTriggerCondition {

    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;
    @Column(name = "TARGET_PRICE", nullable = false, precision = 19, scale = 8)
    private BigDecimal targetPrice;
}
