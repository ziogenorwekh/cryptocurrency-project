package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMinuteResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleMonthResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.candle.CandleWeekResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
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
    public List<CandleMinuteTrackResponse> findCandleMinute(@Valid CandleMinuteTrackQuery candleMinuteTrackQuery) {
        List<CandleMinuteResponseDto> candleMinuteByMarket =
                tradingTrackUseCase.findCandleMinuteByMarket(candleMinuteTrackQuery);
        return candleMinuteByMarket.stream().map(tradingDataMapper::candleMinuteResponseDtoToCandleMinuteTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleDayTrackResponse> findCandleDay(@Valid CandleTrackQuery candleTrackQuery) {
        List<CandleDayResponseDto> candleDayByMarket =
                tradingTrackUseCase.findCandleDayByMarket(candleTrackQuery);
        return candleDayByMarket.stream().map(tradingDataMapper::candleDayResponseDtoToCandleDayTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleWeekTrackResponse> findCandleWeek(@Valid CandleTrackQuery candleTrackQuery) {
        List<CandleWeekResponseDto> candleWeekByMarket =
                tradingTrackUseCase.findCandleWeekByMarket(candleTrackQuery);
        return candleWeekByMarket.stream().map(tradingDataMapper::candleWeekResponseDtoToCandleWeekTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandleMonthTrackResponse> findCandleMonth(@Valid CandleTrackQuery candleTrackQuery) {
        List<CandleMonthResponseDto> candleMonthByMarket =
                tradingTrackUseCase.findCandleMonthByMarket(candleTrackQuery);
        return candleMonthByMarket.stream().map(tradingDataMapper::candleMonthResponseDtoToCandleMonthTrackResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TickerTrackResponse findMarketTicker(TickerTrackQuery tickerTrackQuery) {
        MarketTickerResponseDto marketTickerByMarket = tradingTrackUseCase
                .findMarketTickerByMarket(tickerTrackQuery);
        return tradingDataMapper.marketTickerResponseDtoToTickerTrackResponse(marketTickerByMarket);
    }

    @Override
    public List<TradeTickResponse> findTradeTick(TradeTickTrackQuery tradeTickTrackQuery) {
        List<TradeTickResponseDto> tradeTickByMarket = tradingTrackUseCase.findTradeTickByMarket(tradeTickTrackQuery);
        return tradeTickByMarket.stream().map(tradingDataMapper::tradeTickResponseDtoToTradeTickResponse)
                .collect(Collectors.toList());
    }

}
