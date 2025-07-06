package shop.shportfolio.trading.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookDto;
import shop.shportfolio.trading.application.ports.output.marketdata.OrderBookApiPort;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisPort;

import java.util.List;

@Component
public class OrderMatchingScheduler {

    private final MarketDataRedisPort marketDataRedisPort;
    private final OrderBookApiPort orderBookApiPort;

    private static final List<String> MARKET_IDS = List.of(
            "BTC-KRW", "ETH-KRW", "XRP-KRW", "ADA-KRW", "LTC-KRW"
    );

    @Autowired
    public OrderMatchingScheduler(MarketDataRedisPort marketDataRedisPort,
                                  OrderBookApiPort orderBookApiPort) {
        this.marketDataRedisPort = marketDataRedisPort;
        this.orderBookApiPort = orderBookApiPort;
    }


    @Async
    @Scheduled(fixedRate = 200)
    public void updateOrderBook() {
        for (String market : MARKET_IDS) {
            OrderBookDto orderBook = orderBookApiPort.getOrderBook(market);
            marketDataRedisPort.saveOrderBook(orderBook);
        }
    }


    // 매칭 엔진도 넣어서 매 시간마다 매칭해야 함
}
