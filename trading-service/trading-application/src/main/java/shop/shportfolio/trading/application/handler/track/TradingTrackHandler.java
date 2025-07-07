package shop.shportfolio.trading.application.handler.track;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;

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
}
