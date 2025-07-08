package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
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
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Slf4j
@Component
public class OrderMatchingScheduler {

    private final ExecuteOrderMatchingUseCase executeOrderMatchingUseCase;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    @Autowired
    public OrderMatchingScheduler(ExecuteOrderMatchingUseCase executeOrderMatchingUseCase,
                                  TradingOrderRedisPort tradingOrderRedisPort) {
        this.executeOrderMatchingUseCase = executeOrderMatchingUseCase;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    @Scheduled(fixedDelayString = "${matching.scheduler.interval-ms}")
    public void runReservationOrder() {
        MarketHardCodingData.marketMap.keySet().forEach(marketId -> {
            try {
                List<ReservationOrder> orders = tradingOrderRedisPort.findReservationOrdersByMarketId(marketId);
                orders.forEach(executeOrderMatchingUseCase::executeReservationOrder);
            } catch (Exception e) {
                log.error("Matching failed message: {}", e.getMessage(), e);
            }
        });
    }

    @Scheduled(fixedDelayString = "${matching.scheduler.interval-ms}")
    public void runLimitOrder() {
        MarketHardCodingData.marketMap.keySet().forEach(marketId -> {
            try {
                List<LimitOrder> orders = tradingOrderRedisPort.findLimitOrdersByMarketId(marketId);
                orders.forEach(executeOrderMatchingUseCase::executeLimitOrder);

            } catch (Exception e) {
                log.error("Matching failed message: {}", e.getMessage(), e);
            }
        });

    }


    // 매칭 엔진도 넣어서 매 시간마다 매칭해야 함
}
