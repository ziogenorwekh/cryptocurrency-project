package shop.shportfolio.trading.application.handler.track;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Trade;

import java.util.List;

@Slf4j
@Component
public class TradingTrackHandler {
    private final TradingRepositoryPort tradingRepositoryPort;


    @Autowired
    public TradingTrackHandler(TradingRepositoryPort tradingRepositoryPort) {
        this.tradingRepositoryPort = tradingRepositoryPort;
    }

    public List<Trade> findTradesByMarketId(String marketId) {
        return tradingRepositoryPort.findTradesByMarketId(marketId);
    }

    public LimitOrder findLimitOrderByOrderId(String orderId) {
        return tradingRepositoryPort.findLimitOrderByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s not found", orderId)));
    }
}
