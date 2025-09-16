package shop.shportfolio.trading.application.handler.track;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.trade.Trade;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
public class MarketDataTrackHandler {

    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;

    public MarketDataTrackHandler(TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort,
                                  TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort) {
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
    }

    public MarketItem findMarketItemByMarketId(String marketId) {
        return tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException(
                        String.format("Market item with id %s not found", marketId)));
    }


    public Optional<Trade> findLatestTrade(String marketId) {
        return tradingTradeRecordRepositoryPort.findTopByMarketIdOrderByCreatedAtDesc(marketId);
    }
    public List<Trade> findTradeTickByMarketId(String marketId, String to, Integer count,
                                               Integer daysAgo) {
        if (to == null || to.isEmpty()) {
            to = Instant.now().toString();
        }
        if (daysAgo == null || daysAgo < 0) {
            daysAgo = 0;
        }
        if(count == null || count < 0) {
            count = 1;
        }
        Instant toInstant = Instant.parse(to);
        LocalDateTime toTime = LocalDateTime.ofInstant(toInstant, ZoneOffset.UTC);
        LocalDateTime fromTime = toTime.minusDays(daysAgo);
        return tradingTradeRecordRepositoryPort
                .findTradesByMarketIdAndCreatedAtBetween(marketId, fromTime, toTime, count);
    }
}
