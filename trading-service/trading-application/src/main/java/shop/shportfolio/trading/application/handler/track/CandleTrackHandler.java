package shop.shportfolio.trading.application.handler.track;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;

@Component
public class CandleTrackHandler {

    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;

    public CandleTrackHandler(BithumbApiPort bithumbApiPort,
                              TradingDtoMapper tradingDtoMapper) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
    }

    public CandleMinuteResponseDto findCandleMinuteByMarketId(Integer unit, String marketId, String to, Integer count) {
        CandleMinuteRequestDto candleMinuteRequestDto = tradingDtoMapper.
                toCandleRequestMinuteDto(unit, marketId, to, count);
        return bithumbApiPort.getCandleMinute(candleMinuteRequestDto);
    }
    public CandleDayResponseDto findCandleDayByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.getCandleDay(candleRequestDto);
    }

    public CandleWeekResponseDto findCandleWeekByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.getCandleWeek(candleRequestDto);
    }

    public CandleMonthResponseDto findCandleMonthByMarketId(String marketId, String to, Integer count) {
        CandleRequestDto candleRequestDto = tradingDtoMapper.toCandleRequestDto(marketId, to, count);
        return bithumbApiPort.getCandleMonth(candleRequestDto);
    }
}
