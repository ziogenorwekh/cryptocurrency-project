package shop.shportfolio.trading.infrastructure.database.jpa.entity.market;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.domain.valueobject.MarketStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "MARKET_ITEM")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class MarketItemEntity {

    @Id
    @Column(name = "MARKET_ID", length = 50, nullable = false, unique = true)
    private String marketId;

    @Column(name = "MARKET_KOREAN_NAME", length = 100, nullable = false)
    private String marketKoreanName;

    @Column(name = "MARKET_ENGLISH_NAME", length = 100, nullable = false)
    private String marketEnglishName;

    @Column(name = "MARKET_WARNING", length = 255)
    private String marketWarning;

    @Column(name = "TICK_PRICE", precision = 19, scale = 8, nullable = false)
    private BigDecimal tickPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "MARKET_STATUS", length = 20, nullable = false)
    private MarketStatus marketStatus;

}
