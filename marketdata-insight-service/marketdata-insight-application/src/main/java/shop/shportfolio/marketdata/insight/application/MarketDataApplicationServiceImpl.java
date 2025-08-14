package shop.shportfolio.marketdata.insight.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.marketdata.insight.application.command.request.CandleMinuteTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.CandleTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.MarketTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.*;
import shop.shportfolio.marketdata.insight.application.dto.candle.*;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.marketdata.insight.application.ports.input.MarketDataTrackUseCase;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class MarketDataApplicationServiceImpl implements MarketDataApplicationService {

    private final MarketDataTrackUseCase marketDataTrackUseCase;
    private final MarketDataDtoMapper marketDataDtoMapper;

    public MarketDataApplicationServiceImpl(MarketDataTrackUseCase marketDataTrackUseCase,
                                            MarketDataDtoMapper marketDataDtoMapper) {
        this.marketDataTrackUseCase = marketDataTrackUseCase;
        this.marketDataDtoMapper = marketDataDtoMapper;
    }

    @Override
    public MarketCodeTrackResponse findMarketByMarketId(MarketTrackQuery query) {
        MarketItem marketItem = marketDataTrackUseCase.findMarketItemByMarketCode(query.getMarketId());
        return marketDataDtoMapper.marketItemToMarketItemTrackResponse(marketItem);
    }

    @Override
    public List<MarketCodeTrackResponse> findAllMarkets() {
        List<MarketItem> allMarketItems = marketDataTrackUseCase.findAllMarketItems();
        return allMarketItems.stream().map(marketDataDtoMapper::marketItemToMarketItemTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleMinuteTrackResponse> findCandleMinute(@Valid CandleMinuteTrackQuery query) {
        CandleMinuteRequestDto dto = marketDataDtoMapper.toCandleRequestMinuteDto(
                query.getUnit(), query.getMarket(), query.getTo(), query.getCount());
        List<CandleMinuteResponseDto> candleMinuteByMarket =
                marketDataTrackUseCase.findCandleMinuteByMarket(dto);
        return candleMinuteByMarket.stream().map(marketDataDtoMapper::candleMinuteResponseDtoToCandleMinuteTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleDayTrackResponse> findCandleDay(@Valid CandleTrackQuery candleTrackQuery) {
        CandleRequestDto dto = marketDataDtoMapper.toCandleRequestDto(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
        List<CandleDayResponseDto> candleDayByMarket =
                marketDataTrackUseCase.findCandleDayByMarket(dto);
        return candleDayByMarket.stream().map(marketDataDtoMapper::candleDayResponseDtoToCandleDayTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleWeekTrackResponse> findCandleWeek(@Valid CandleTrackQuery candleTrackQuery) {
        CandleRequestDto dto = marketDataDtoMapper.toCandleRequestDto(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
        List<CandleWeekResponseDto> candleWeekByMarket =
                marketDataTrackUseCase.findCandleWeekByMarket(dto);
        return candleWeekByMarket.stream().map(marketDataDtoMapper::candleWeekResponseDtoToCandleWeekTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleMonthTrackResponse> findCandleMonth(@Valid CandleTrackQuery candleTrackQuery) {
        CandleRequestDto dto = marketDataDtoMapper.toCandleRequestDto(candleTrackQuery.getMarketId(),
                candleTrackQuery.getTo(), candleTrackQuery.getCount());
        List<CandleMonthResponseDto> candleMonthByMarket =
                marketDataTrackUseCase.findCandleMonthByMarket(dto);
        return candleMonthByMarket.stream().map(marketDataDtoMapper::candleMonthResponseDtoToCandleMonthTrackResponse)
                .collect(Collectors.toList());
    }
}
