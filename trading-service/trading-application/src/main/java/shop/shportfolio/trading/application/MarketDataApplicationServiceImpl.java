package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.track.*;
import shop.shportfolio.trading.application.dto.marketdata.CandleTrackQuery;
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
    public MarketCodeTrackResponse findMarketById(MarketTrackQuery marketTrackQuery) {
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
    public CandleMinuteTrackResponse findCandleMinute(CandleMinuteTrackQuery candleMinuteTrackQuery) {
        return null;
    }

    @Override
    public CandleDayTrackResponse findCandleDay(CandleTrackQuery candleTrackQuery) {
        return null;
    }

    @Override
    public CandleWeekTrackResponse findCandleWeek(CandleTrackQuery candleTrackQuery) {
        return null;
    }

    @Override
    public CandleMonthTrackResponse findCandleMonth(CandleTrackQuery candleTrackQuery) {
        return null;
    }

}
