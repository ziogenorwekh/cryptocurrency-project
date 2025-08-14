package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;

@Slf4j
@Component
public class OrderBookScheduler {
    private final BithumbApiPort bithumbApiPort;
    private final TradingMarketDataRedisPort tradingMarketDataRedisPort;


    public OrderBookScheduler(BithumbApiPort bithumbApiPort,
                              TradingMarketDataRedisPort tradingMarketDataRedisPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingMarketDataRedisPort = tradingMarketDataRedisPort;
    }

    @Scheduled(fixedDelayString = "${update.orderbook.scheduler.interval-ms}")
    public void updateOrderBook() {
        MarketHardCodingData.marketMap.forEach((market, marketId) -> {
            try {
                OrderBookBithumbDto orderBook = bithumbApiPort.findOrderBookByMarketId(market);
                tradingMarketDataRedisPort.saveOrderBook(
                        RedisKeyPrefix.orderBook(market),
                        orderBook
                );
                log.debug("OrderBook updated in Redis: {}", market);
            } catch (Exception ex) {
                log.error("Failed to update OrderBook for market: {}", market, ex);
            }
        });
    }

}
