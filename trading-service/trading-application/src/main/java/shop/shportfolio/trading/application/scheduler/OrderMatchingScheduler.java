package shop.shportfolio.trading.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.ports.input.ExecuteOrderMatchingUseCase;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;

import java.util.List;

@Component
public class OrderMatchingScheduler {

    private final ExecuteOrderMatchingUseCase  executeOrderMatchingUseCase;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    @Autowired
    public OrderMatchingScheduler(ExecuteOrderMatchingUseCase executeOrderMatchingUseCase,
                                  TradingOrderRedisPort tradingOrderRedisPort) {
        this.executeOrderMatchingUseCase = executeOrderMatchingUseCase;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    @Async
    @Scheduled(fixedRate = 500)
    public void runReservationOrder() {

    }

    @Async
    @Scheduled(fixedRate = 500)
    public void runLimitOrder() {
    }


    // 매칭 엔진도 넣어서 매 시간마다 매칭해야 함
}
