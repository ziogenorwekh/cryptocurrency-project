package shop.shportfolio.trading.application.initializer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;

@Component
public class InitializeOrderInRedis {

    private final TradingOrderRedisPort tradingOrderRedisPort;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;

    public InitializeOrderInRedis(TradingOrderRedisPort tradingOrderRedisPort,
                                  TradingOrderRepositoryPort tradingOrderRepositoryPort) {
        this.tradingOrderRedisPort = tradingOrderRedisPort;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
    }


    @PostConstruct
    public void init() {
        loadKeyOnInMemoryData();
    }


    private void loadKeyOnInMemoryData() {

    }
}
