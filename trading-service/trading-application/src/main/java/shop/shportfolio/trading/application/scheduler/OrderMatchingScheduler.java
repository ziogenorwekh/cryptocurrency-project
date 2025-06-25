package shop.shportfolio.trading.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;

@Component
public class OrderMatchingScheduler {

    private final MarketDataRedisAdapter marketDataRedisAdapter;
    private final TemporaryKafkaPublisher temporaryKafkaPublisher;
    @Autowired
    public OrderMatchingScheduler(MarketDataRedisAdapter marketDataRedisAdapter,
                                  TemporaryKafkaPublisher temporaryKafkaPublisher) {
        this.marketDataRedisAdapter = marketDataRedisAdapter;
        this.temporaryKafkaPublisher = temporaryKafkaPublisher;
    }

}
