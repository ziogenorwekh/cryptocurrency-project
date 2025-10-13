package shop.shportfolio.marketdata.insight.application.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.AiCandleAnalysisUseCase;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

@Slf4j
@Component
public class AiCandleAnalysisScheduler {

    private final AiCandleAnalysisUseCase analysisUseCase;

    public AiCandleAnalysisScheduler(AiCandleAnalysisUseCase analysisUseCase) {
        this.analysisUseCase = analysisUseCase;
    }

    //@PostConstruct
    public void init() {
        thirtyMinutesAnalysis();
    }

    @Scheduled(cron = "0 2/30 * * * *", zone = "UTC")
    public void thirtyMinutesAnalysis() {
        analysisUseCase.analyzeMarketData(PeriodType.THIRTY_MINUTES);
    }

    @Scheduled(cron = "0 2/30 * * * *", zone = "UTC")
    public void dailyAnalysis() {
        analysisUseCase.analyzeMarketData(PeriodType.ONE_DAY);
    }

}