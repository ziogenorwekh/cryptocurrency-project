package shop.shportfolio.marketdata.insight.application.ports.input.usecase.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.exception.MarketItemNotFoundException;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.MarketDataTrackUseCase;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.MarketItemRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

import java.util.List;

@Component
public class MarketDataTrackUseCaseImpl implements MarketDataTrackUseCase {

    private final BithumbApiPort bithumbApiPort;
    private final MarketItemRepositoryPort repositoryPort;
    @Autowired
    public MarketDataTrackUseCaseImpl(BithumbApiPort bithumbApiPort,
                                      MarketItemRepositoryPort repositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.repositoryPort = repositoryPort;
    }

    @Override
    public MarketItem findMarketItemByMarketCode(String marketId) {
        return repositoryPort.findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("Market Item with id %s not found", marketId)
                ));
    }

    @Override
    public List<MarketItem> findAllMarketItems() {
        return repositoryPort.findAllMarketItems();
    }

    @Override
    public List<CandleDayResponseDto> findCandleDayByMarket(CandleRequestDto dto) {
        return bithumbApiPort.findCandleDays(dto);
    }

    @Override
    public List<CandleWeekResponseDto> findCandleWeekByMarket(CandleRequestDto dto) {
        return bithumbApiPort.findCandleWeeks(dto);
    }

    @Override
    public List<CandleMonthResponseDto> findCandleMonthByMarket(CandleRequestDto dto) {
        return bithumbApiPort.findCandleMonths(dto);
    }

    @Override
    public List<CandleMinuteResponseDto> findCandleMinuteByMarket(CandleMinuteRequestDto dto) {
        return bithumbApiPort.findCandleMinutes(dto);
    }
}
