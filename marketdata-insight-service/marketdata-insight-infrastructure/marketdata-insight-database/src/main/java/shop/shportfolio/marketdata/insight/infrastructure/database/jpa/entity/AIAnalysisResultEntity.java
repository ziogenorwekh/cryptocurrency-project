package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "AI_ANALYSIS_RESULT_ENTITY")
@NoArgsConstructor
public class AIAnalysisResultEntity {


    @Id
    @Column(name = "AI_ANALYSIS_RESULT_ID", nullable = false)
    private UUID aiAnalysisResultId;

    @Setter(AccessLevel.PROTECTED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MARKET_ID", nullable = false)
    private MarketItemEntity marketItemEntity;

    @Column(name = "ANALYSIS_TIME", nullable = false)
    private LocalDateTime analysisTime;

    @Column(name = "MOMENTUM_SCORE", precision = 19, scale = 8)
    private BigDecimal momentumScore;

    @Column(name = "PERIOD_END")
    private LocalDateTime periodEnd;

    @Column(name = "PERIOD_START")
    private LocalDateTime periodStart;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERIOD_TYPE", length = 20)
    private PeriodType periodType;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRICE_TREND", length = 20)
    private PriceTrend priceTrend;

    @Enumerated(EnumType.STRING)
    @Column(name = "SIGNAL", length = 20)
    private Signal signal;

    @Column(name = "SUMMARY_COMMENT", length = 500)
    private String summaryComment;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID aiAnalysisResultId;
        private MarketItemEntity marketItemEntity;
        private LocalDateTime analysisTime;
        private BigDecimal momentumScore;
        private LocalDateTime periodEnd;
        private LocalDateTime periodStart;
        private PeriodType periodType;
        private PriceTrend priceTrend;
        private Signal signal;
        private String summaryComment;

        public Builder aiAnalysisResultId(UUID aiAnalysisResultId) {
            this.aiAnalysisResultId = aiAnalysisResultId;
            return this;
        }

        public Builder marketItemEntity(MarketItemEntity marketItemEntity) {
            this.marketItemEntity = marketItemEntity;
            return this;
        }

        public Builder analysisTime(LocalDateTime analysisTime) {
            this.analysisTime = analysisTime;
            return this;
        }

        public Builder momentumScore(BigDecimal momentumScore) {
            this.momentumScore = momentumScore;
            return this;
        }

        public Builder periodEnd(LocalDateTime periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        public Builder periodStart(LocalDateTime periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        public Builder periodType(PeriodType periodType) {
            this.periodType = periodType;
            return this;
        }

        public Builder priceTrend(PriceTrend priceTrend) {
            this.priceTrend = priceTrend;
            return this;
        }

        public Builder signal(Signal signal) {
            this.signal = signal;
            return this;
        }

        public Builder summaryComment(String summaryComment) {
            this.summaryComment = summaryComment;
            return this;
        }

        public AIAnalysisResultEntity build() {
            AIAnalysisResultEntity entity = new AIAnalysisResultEntity();
            entity.aiAnalysisResultId = this.aiAnalysisResultId;
            entity.marketItemEntity = this.marketItemEntity;
            entity.analysisTime = this.analysisTime;
            entity.momentumScore = this.momentumScore;
            entity.periodEnd = this.periodEnd;
            entity.periodStart = this.periodStart;
            entity.periodType = this.periodType;
            entity.priceTrend = this.priceTrend;
            entity.signal = this.signal;
            entity.summaryComment = this.summaryComment;
            return entity;
        }
    }

}
