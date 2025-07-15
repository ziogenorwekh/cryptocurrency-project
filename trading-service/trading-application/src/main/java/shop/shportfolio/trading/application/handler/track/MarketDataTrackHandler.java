package shop.shportfolio.trading.application.handler.track;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.trade.TradeTickResponseDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.Trade;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
public class MarketDataTrackHandler {

    private final BithumbApiPort bithumbApiPort;
    private final TradingDtoMapper tradingDtoMapper;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;

    public MarketDataTrackHandler(BithumbApiPort bithumbApiPort,
                                  TradingDtoMapper tradingDtoMapper,
                                  TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort,
                                  TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
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
        MarketTickerResponseDto apiPortResult = bithumbApiPort.
                findTickerByMarketId(new MarketTickerRequestDto(marketId));
        Optional<Trade> top = tradingTradeRecordRepositoryPort.findTopByMarketIdOrderByCreatedAtDesc(marketId);

        System.out.println("API timestamp: " + apiPortResult.getTradeTimestamp());
        if (top.isPresent()) {
            Trade trade = top.get();
            long tradeTimestamp = trade.getCreatedAt().getValue()
                    .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
            System.out.println("Trade timestamp: " + tradeTimestamp);

            if (tradeTimestamp > apiPortResult.getTradeTimestamp()) {
                MarketTickerResponseDto updatedDto = tradingDtoMapper.tradeToMarketTickerResponseDto(trade, apiPortResult);
                System.out.println("Updated tradePrice: " + updatedDto.getTradePrice());
                System.out.println("Updated tradeVolume: " + updatedDto.getTradeVolume());
                return updatedDto;
            }
        }
        return apiPortResult;
    }
    public List<TradeTickResponseDto> findTradeTickByMarketId(String marketId, String to, Integer count,
                                                              String cursor, Integer daysAgo) {
        TradeTickRequestDto tradeTickRequestDto = tradingDtoMapper.toTradeTickRequestDto(marketId, to, count, cursor, daysAgo);
        return bithumbApiPort.findTradeTicks(tradeTickRequestDto);
    }
}
