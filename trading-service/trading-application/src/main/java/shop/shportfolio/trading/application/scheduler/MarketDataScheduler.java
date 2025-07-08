package shop.shportfolio.trading.application.scheduler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;

import java.util.List;
import java.util.Map;

@Component
public class MarketDataScheduler {
    private final BithumbApiPort bithumbApiPort;
    private final TradingMarketDataRedisPort tradingMarketDataRedisPort;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;


    private static final Map<String, Integer> marketMap = Map.ofEntries(
            Map.entry("BTC-KRW", 10000),
            Map.entry("ETH-KRW", 1000),
            Map.entry("XRP-KRW", 1),
            Map.entry("ADA-KRW", 1),
            Map.entry("DOGE-KRW", 1),
            Map.entry("BCH-KRW", 500),
            Map.entry("TRX-KRW", 1),
            Map.entry("XLM-KRW", 1),
            Map.entry("LINK-KRW", 10),
            Map.entry("DOT-KRW", 1),
            Map.entry("SAND-KRW", 1),
            Map.entry("SOL-KRW", 100),
            Map.entry("ATOM-KRW", 5),
            Map.entry("ALGO-KRW", 1)
    );

    public MarketDataScheduler(BithumbApiPort bithumbApiPort,
                               TradingMarketDataRedisPort tradingMarketDataRedisPort,
                               TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingMarketDataRedisPort = tradingMarketDataRedisPort;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }

    @Async
    @Scheduled(fixedRate = 200)
    public void updateOrderBook() {
        for (String market : marketMap.keySet()) {
            OrderBookBithumbDto orderBook = bithumbApiPort.getOrderBook(market);
            tradingMarketDataRedisPort.saveOrderBook(RedisKeyPrefix.orderBook(market), orderBook);
        }
    }

    @Async
    @Scheduled(cron = "0 0 0 * * MON")
    public void saveMarketCode() {
        for (String market : marketMap.keySet()) {
            MarketItemBithumbDto marketItem = bithumbApiPort.getMarketItem(market);

        }
    }
}
