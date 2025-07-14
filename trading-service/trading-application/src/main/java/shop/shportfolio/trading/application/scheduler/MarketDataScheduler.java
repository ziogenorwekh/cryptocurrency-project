package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.MarketItem;

@Slf4j
@Component
public class MarketDataScheduler {
    private final BithumbApiPort bithumbApiPort;
    private final TradingMarketDataRedisPort tradingMarketDataRedisPort;
    private final TradingDtoMapper tradingDtoMapper;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;


    public MarketDataScheduler(BithumbApiPort bithumbApiPort,
                               TradingMarketDataRedisPort tradingMarketDataRedisPort,
                               TradingDtoMapper tradingDtoMapper,
                               TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.tradingMarketDataRedisPort = tradingMarketDataRedisPort;
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
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

    @Scheduled(cron = "0 0 0 * * MON")
    public void saveMarketCode() {
        MarketHardCodingData.marketMap.forEach((market, marketId) -> {
            try {
                MarketItemBithumbDto dto = bithumbApiPort.findMarketItemByMarketId(market);
                MarketItem entity = tradingDtoMapper.marketItemBithumbDtoToMarketItem(dto, marketId);
                tradingMarketDataRepositoryPort.saveMarketItem(entity);
                log.info("MarketItem saved: {}", market);
            } catch (Exception ex) {
                log.error("Failed to save MarketItem for market: {}", market, ex);
            }
        });
    }
}
