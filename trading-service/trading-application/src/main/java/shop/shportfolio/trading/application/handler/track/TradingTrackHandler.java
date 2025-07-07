package shop.shportfolio.trading.application.handler.track;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TradingTrackHandler {
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;

    @Autowired
    public TradingTrackHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                               TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
    }

    public List<Trade> findTradesByMarketId(String marketId) {
        return tradingTradeRecordRepositoryPort.findTradesByMarketId(marketId);
    }

    public LimitOrder findLimitOrderByOrderId(String orderId) {
        return tradingOrderRepositoryPort.findLimitOrderByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s not found", orderId)));
    }

    public LimitOrder findLimitOrderById(String orderId, UUID userId) {
        return tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s not found", orderId)));
    }

    public ReservationOrder findReservationOrderByOrderIdAndUserId(
            String orderId, UUID userId) {
        return tradingOrderRepositoryPort.findReservationOrderByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s not found", orderId)));
    }
}
