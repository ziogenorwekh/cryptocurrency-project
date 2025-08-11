package shop.shportfolio.trading.infrastructure.database.jpa.entity.market;

import jakarta.persistence.*;
import lombok.*;
import shop.shportfolio.trading.domain.valueobject.MarketStatus;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "MARKET_ITEM")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String marketId;
        private String marketKoreanName;
        private String marketEnglishName;
        private String marketWarning;
        private BigDecimal tickPrice;
        private MarketStatus marketStatus;

        public Builder marketId(String marketId) {
            this.marketId = marketId;
            return this;
        }
        public Builder marketKoreanName(String marketKoreanName) {
            this.marketKoreanName = marketKoreanName;
            return this;
        }
        public Builder marketEnglishName(String marketEnglishName) {
            this.marketEnglishName = marketEnglishName;
            return this;
        }
        public Builder marketWarning(String marketWarning) {
            this.marketWarning = marketWarning;
            return this;
        }
        public Builder marketStatus(MarketStatus marketStatus) {
            this.marketStatus = marketStatus;
            return this;
        }
        public Builder tickPrice(BigDecimal tickPrice) {
            this.tickPrice = tickPrice;
            return this;
        }
        public MarketItemEntity build() {
            return new MarketItemEntity(
                    marketId,marketKoreanName,marketEnglishName,marketWarning
                    ,tickPrice,marketStatus);
        }
    }
}
