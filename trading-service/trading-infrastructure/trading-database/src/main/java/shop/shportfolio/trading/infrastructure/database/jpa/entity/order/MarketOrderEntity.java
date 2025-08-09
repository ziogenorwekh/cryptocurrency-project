package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@Table(name = "MARKET_ORDER")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("market")
@PrimaryKeyJoinColumn(name = "MARKET_ORDER_ID")
public class MarketOrderEntity extends OrderEntity {

    @Column(name = "REMAINING_PRICE", nullable = false, precision = 19, scale = 8)
    private BigDecimal remainingPrice;

}
