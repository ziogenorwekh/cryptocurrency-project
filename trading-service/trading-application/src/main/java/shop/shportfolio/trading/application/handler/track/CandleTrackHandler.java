package shop.shportfolio.trading.application.handler.track;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;

import java.util.List;

@Component
public class CandleTrackHandler {

    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;

    public CandleTrackHandler(BithumbApiPort bithumbApiPort,
                              TradingDtoMapper tradingDtoMapper) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
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
}
