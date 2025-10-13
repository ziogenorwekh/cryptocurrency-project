package shop.shportfolio.marketdata.insight.application.ports.input.usecase;

import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

public interface AiCandleAnalysisUseCase {

    void analyzeMarketData(PeriodType periodType);
}
