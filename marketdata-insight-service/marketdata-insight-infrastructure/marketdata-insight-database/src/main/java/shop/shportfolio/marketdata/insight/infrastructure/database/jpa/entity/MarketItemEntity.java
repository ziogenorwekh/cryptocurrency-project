package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.MarketEnglishName;
import shop.shportfolio.common.domain.valueobject.MarketKoreanName;
import shop.shportfolio.common.domain.valueobject.MarketStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "MARKET_ITEM_ENTITY")
@NoArgsConstructor
public class MarketItemEntity {

    @Id
    @Column(name = "MARKET_ID", nullable = false, length = 50)
    private String marketId;

    @Column(name = "MARKET_KOREAN_NAME", nullable = false, length = 100)
    private String marketKoreanName;

    @Column(name = "MARKET_ENGLISH_NAME", nullable = false, length = 100)
    private String marketEnglishName;

    @Enumerated(EnumType.STRING)
    @Column(name = "MARKET_STATUS", nullable = false, length = 20)
    private MarketStatus marketStatus;

    @OneToMany(mappedBy = "marketItemEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AIAnalysisResultEntity> aiAnalysisResults = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String marketId;
        private String marketKoreanName;
        private String marketEnglishName;
        private MarketStatus marketStatus;
        private List<AIAnalysisResultEntity> aiAnalysisResults = new ArrayList<>();

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

        public Builder marketStatus(MarketStatus marketStatus) {
            this.marketStatus = marketStatus;
            return this;
        }

        public Builder aiAnalysisResults(List<AIAnalysisResultEntity> aiAnalysisResults) {
            if (aiAnalysisResults != null) {
                this.aiAnalysisResults = aiAnalysisResults;
            }
            return this;
        }

        public MarketItemEntity build() {
            MarketItemEntity entity = new MarketItemEntity();
            entity.marketId = this.marketId;
            entity.marketKoreanName = this.marketKoreanName;
            entity.marketEnglishName = this.marketEnglishName;
            entity.marketStatus = this.marketStatus;
            entity.aiAnalysisResults = this.aiAnalysisResults;
            this.aiAnalysisResults.forEach(result -> result.setMarketItemEntity(entity));
            return entity;
        }
    }
    public void addAIAnalysisResult(AIAnalysisResultEntity aiAnalysisResult) {
        aiAnalysisResults.add(aiAnalysisResult);
        aiAnalysisResult.setMarketItemEntity(this);
    }

    public void removeAIAnalysisResult(AIAnalysisResultEntity aiAnalysisResult) {
        aiAnalysisResults.remove(aiAnalysisResult);
        aiAnalysisResult.setMarketItemEntity(null);
    }
}
