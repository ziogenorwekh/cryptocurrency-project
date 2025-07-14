package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.track.request.CandleMinuteTrackQuery;
import shop.shportfolio.trading.application.command.track.request.CandleTrackQuery;
import shop.shportfolio.trading.application.command.track.request.MarketTrackQuery;
import shop.shportfolio.trading.application.command.track.response.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMonthResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleWeekResponseDto;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;
import shop.shportfolio.trading.domain.entity.MarketItem;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class MarketDataApplicationServiceImpl implements MarketDataApplicationService {


    private final TradingTrackUseCase tradingTrackUseCase;
    private final TradingDataMapper tradingDataMapper;

    public MarketDataApplicationServiceImpl(TradingTrackUseCase tradingTrackUseCase,
                                            TradingDataMapper tradingDataMapper) {
        this.tradingTrackUseCase = tradingTrackUseCase;
        this.tradingDataMapper = tradingDataMapper;
    }

    @Override
    public MarketCodeTrackResponse findMarketById(@Valid MarketTrackQuery marketTrackQuery) {
        MarketItem item = tradingTrackUseCase.findMarketItemByMarketItemId(marketTrackQuery);
        return tradingDataMapper.marketItemToMarketItemTrackResponse(item);
    }

    @Override
    public List<MarketCodeTrackResponse> findAllMarkets() {
        List<MarketItem> allMarketItems = tradingTrackUseCase.findAllMarketItems();
        return allMarketItems.stream().map(tradingDataMapper::marketItemToMarketItemTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandleMinuteTrackResponse findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery) {
        CandleMinuteResponseDto candleMinuteByMarket = tradingTrackUseCase
                .findCandleMinuteByMarket(candleMinuteTrackQuery);
        return tradingDataMapper.candleMinuteResponseDtoToCandleMinuteTrackResponse(candleMinuteByMarket);
    }

    @Override
    public CandleDayTrackResponse findCandleDay(@Valid CandleTrackQuery candleTrackQuery) {
        CandleDayResponseDto candleDayByMarket = tradingTrackUseCase.findCandleDayByMarket(candleTrackQuery);
        return tradingDataMapper.candleDayResponseDtoToCandleDayTrackResponse(candleDayByMarket);
    }

    @Override
    public CandleWeekTrackResponse findCandleWeek(@Valid CandleTrackQuery candleTrackQuery) {
        CandleWeekResponseDto candleWeekByMarket = tradingTrackUseCase.findCandleWeekByMarket(candleTrackQuery);
        return tradingDataMapper.candleWeekResponseDtoToCandleWeekTrackResponse(candleWeekByMarket);
    }

    @Override
    public CandleMonthTrackResponse findCandleMonth(@Valid CandleTrackQuery candleTrackQuery) {
        CandleMonthResponseDto candleMonthByMarket = tradingTrackUseCase.findCandleMonthByMarket(candleTrackQuery);
        return tradingDataMapper.candleMonthResponseDtoToCandleMonthTrackResponse(candleMonthByMarket);
    }

}
