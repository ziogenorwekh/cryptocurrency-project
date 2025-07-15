package shop.shportfolio.trading.application.handler.track;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.domain.entity.MarketItem;

import java.util.List;

@Component
public class MarketDataTrackHandler {

    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;

    public MarketDataTrackHandler(BithumbApiPort bithumbApiPort,
                                  TradingDtoMapper tradingDtoMapper,
                                  TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }

    public List<CandleMinuteResponseDto> findCandleMinuteByMarketId(Integer unit, String marketId, String to, Integer count) {
        CandleMinuteRequestDto candleMinuteRequestDto = tradingDtoMapper.
                toCandleRequestMinuteDto(unit, marketId, to, count);
        return bithumbApiPort.findCandleMinutes(candleMinuteRequestDto);
    }
    public List<CandleDayResponseDto> findCandleDayByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.findCandleDays(candleRequestDto);
    }

    public List<CandleWeekResponseDto> findCandleWeekByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.findCandleWeeks(candleRequestDto);
    }

    public List<CandleMonthResponseDto> findCandleMonthByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.findCandleMonths(candleRequestDto);
    }

    public MarketItem findMarketItemByMarketId(String marketId) {
        return tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("Market item with id %s not found", marketId)));
    }

    public List<MarketItem> findAllMarketItems() {
        return tradingMarketDataRepositoryPort.findAllMarketItems();
    }

    public MarketTickerResponseDto findMarketTickerByMarketId(String marketId) {
        return bithumbApiPort.findTickerByMarketId(new MarketTickerRequestDto(marketId));
    }
}
