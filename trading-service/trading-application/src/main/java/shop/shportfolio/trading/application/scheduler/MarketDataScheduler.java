package shop.shportfolio.trading.application.scheduler;

import org.springframework.scheduling.annotation.Async;
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

import java.util.Map;

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

    @Async
    @Scheduled(fixedRate = 200)
    public void updateOrderBook() {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            OrderBookBithumbDto orderBook = bithumbApiPort.getOrderBook(market);
            tradingMarketDataRedisPort.saveOrderBook(RedisKeyPrefix.orderBook(market), orderBook);
        }
    }

    @Async
    @Scheduled(cron = "0 0 0 * * MON")
    public void saveMarketCode() {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            MarketItemBithumbDto marketItemBithumbDto = bithumbApiPort.getMarketItem(market);
            MarketItem marketItem = tradingDtoMapper.
                    marketItemBithumbDtoToMarketItem(marketItemBithumbDto,
                            MarketHardCodingData.marketMap.get(market));
            tradingMarketDataRepositoryPort.saveMarketItem(marketItem);
        }
    }
}
