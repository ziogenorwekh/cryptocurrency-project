package shop.shportfolio.trading.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.orderbook.matching.OrderMatchingExecutor;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Slf4j
@Component
public class OrderMatchingScheduler {

    private final OrderMatchingExecutor orderMatchingExecutor;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    @Autowired
    public OrderMatchingScheduler(OrderMatchingExecutor orderMatchingExecutor,
                                  TradingOrderRedisPort tradingOrderRedisPort) {
        this.orderMatchingExecutor = orderMatchingExecutor;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    /**
     * 단일 스레드에서 모든 주문 타입을 순차적으로 처리
     */
    @Scheduled(fixedDelayString = "${matching.scheduler.interval-ms}")
    public void runAllMatching() {
        MarketHardCodingData.marketMap.keySet().forEach(marketId -> {
            try {
                // 1. 예약주문 처리
                List<ReservationOrder> reservationOrders = tradingOrderRedisPort.findReservationOrdersByMarketId(marketId);
                reservationOrders.forEach(orderMatchingExecutor::executeReservationOrder);

                // 2. 리밋 주문 처리
                List<LimitOrder> limitOrders = tradingOrderRedisPort.findLimitOrdersByMarketId(marketId);
                limitOrders.forEach(orderMatchingExecutor::executeLimitOrder);

                // 3. 마켓 주문 처리
                List<MarketOrder> marketOrders = tradingOrderRedisPort.findMarketOrdersByMarketId(marketId);
                marketOrders.forEach(orderMatchingExecutor::executeMarketOrder);

            } catch (Exception e) {
                log.error("Order matching failed for marketId {}: {}", marketId, e.getMessage(), e);
            }
        });
    }
}
