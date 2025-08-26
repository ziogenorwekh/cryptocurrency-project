package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.ports.input.ExecuteOrderMatchingUseCase;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
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
}
