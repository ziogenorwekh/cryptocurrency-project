package shop.shportfolio.trading.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.OrderBookApiPort;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;

import java.util.List;

@Component
public class OrderMatchingScheduler {

    private final MarketDataRedisAdapter marketDataRedisAdapter;
    private final OrderBookApiPort orderBookApiPort;

    private static final List<String> MARKET_IDS = List.of(
            "BTC-KRW", "ETH-KRW", "XRP-KRW", "ADA-KRW", "LTC-KRW"
    );

    @Autowired
    public OrderMatchingScheduler(MarketDataRedisAdapter marketDataRedisAdapter,
                                  OrderBookApiPort orderBookApiPort) {
        this.marketDataRedisAdapter = marketDataRedisAdapter;
        this.orderBookApiPort = orderBookApiPort;
    }


    @Async
    @Scheduled(fixedRate = 200)
    public void updateOrderBook() {
        for (String market : MARKET_IDS) {
            OrderBookDto orderBook = orderBookApiPort.getOrderBook(market);
            marketDataRedisAdapter.saveOrderBook(orderBook);
        }
    }


    // 매칭 엔진도 넣어서 매 시간마다 매칭해야 함
}
