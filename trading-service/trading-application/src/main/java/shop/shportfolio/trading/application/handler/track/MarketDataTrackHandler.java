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
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.trade.Trade;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        if (top.isPresent()) {
            Trade trade = top.get();
            long tradeTimestamp = trade.getCreatedAt().getValue()
                    .atZone(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli();

            if (tradeTimestamp > apiPortResult.getTimestamp()) {
                MarketTickerResponseDto updatedDto = tradingDtoMapper.tradeToMarketTickerResponseDto(trade, apiPortResult);
                return updatedDto;
            }
        }
        return apiPortResult;
    }
    public List<TradeTickResponseDto> findTradeTickByMarketId(String marketId, String to, Integer count,
                                                              String cursor, Integer daysAgo) {
        if (to == null || to.isEmpty()) {
            to = Instant.now().toString();
        }
        if (daysAgo == null) {
            daysAgo = 0;
        }
        TradeTickRequestDto tradeTickRequestDto = tradingDtoMapper.toTradeTickRequestDto(marketId, to, count, cursor, daysAgo);
        List<TradeTickResponseDto> bithumbApiPortTradeTicks = bithumbApiPort.findTradeTicks(tradeTickRequestDto);

        Instant toInstant = Instant.parse(to);
        LocalDateTime toTime = LocalDateTime.ofInstant(toInstant, ZoneOffset.UTC);
        LocalDateTime fromTime = toTime.minusDays(daysAgo);

        List<Trade> internalTrades = tradingTradeRecordRepositoryPort
                .findTradesByMarketIdAndCreatedAtBetween(marketId, fromTime, toTime, count);

        List<TradeTickResponseDto> parsing = internalTrades.stream()
                .map(tradingDtoMapper::tradeToTradeTickResponseDto)
                .collect(Collectors.toList());

        List<TradeTickResponseDto> merged = new ArrayList<>();
        merged.addAll(parsing);
        merged.addAll(bithumbApiPortTradeTicks);
        return merged;
    }
}
