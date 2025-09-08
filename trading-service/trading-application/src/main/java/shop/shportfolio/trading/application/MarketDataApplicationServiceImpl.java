package shop.shportfolio.trading.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.trading.application.command.track.request.*;
import shop.shportfolio.trading.application.command.track.response.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.MarketDataApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingTrackUseCase;

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
    @Transactional
    public TickerTrackResponse findMarketTicker(@Valid TickerTrackQuery tickerTrackQuery) {
        MarketTickerResponseDto marketTickerByMarket = tradingTrackUseCase
                .findMarketTickerByMarket(tickerTrackQuery);
        return tradingDataMapper.marketTickerResponseDtoToTickerTrackResponse(marketTickerByMarket);
    }

    @Override
    @Transactional
    public List<TradeTickTrackResponse> findTradeTick(@Valid TradeTickTrackQuery tradeTickTrackQuery) {
        List<TradeTickResponseDto> tradeTickByMarket = tradingTrackUseCase.findTradeTickByMarket(tradeTickTrackQuery);
        return tradeTickByMarket.stream().map(tradingDataMapper::tradeTickResponseDtoToTradeTickResponse)
                .collect(Collectors.toList());
    }

}
