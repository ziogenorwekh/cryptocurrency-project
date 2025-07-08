package shop.shportfolio.trading.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;

import java.util.List;

@Component
public class OrderMatchingScheduler {

    private final TradingOrderRedisPort tradingOrderRedisPort;
    private final BithumbApiPort bithumbApiPort;
    private final TradingMarketDataRedisPort tradingMarketDataRedisPort;


    private static final List<String> MARKET_IDS = List.of(
            "BTC-KRW", "ETH-KRW", "XRP-KRW", "ADA-KRW", "LTC-KRW"
    );

    @Autowired
    public OrderMatchingScheduler(TradingOrderRedisPort tradingOrderRedisPort,
                                  BithumbApiPort bithumbApiPort,
                                  TradingMarketDataRedisPort tradingMarketDataRedisPort) {
        this.tradingOrderRedisPort = tradingOrderRedisPort;
        this.bithumbApiPort = bithumbApiPort;
        this.tradingMarketDataRedisPort = tradingMarketDataRedisPort;
    }


    @Async
    @Scheduled(fixedRate = 200)
    public void updateOrderBook() {
        // 틱가격도 저장해야 함
        for (String market : MARKET_IDS) {
            OrderBookBithumbDto orderBook = bithumbApiPort.getOrderBook(market);
            tradingMarketDataRedisPort.saveOrderBook(RedisKeyPrefix.orderBook(market), orderBook);
        }
    }

    @Async
    @Scheduled(fixedRate = 500)
    public void runReservationOrder() {

    }


    // 매칭 엔진도 넣어서 매 시간마다 매칭해야 함
}
