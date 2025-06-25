package shop.shportfolio.trading.application.handler.create.strategy.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.handler.create.strategy.OrderExecutionStrategy;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Deprecated
public class OrderExecutionMarketAsks implements OrderExecutionStrategy<OrderBookAsksDto,MarketOrder> {

    private final TradingDomainService tradingDomainService;

    @Autowired
    public OrderExecutionMarketAsks(TradingDomainService tradingDomainService) {
        this.tradingDomainService = tradingDomainService;
    }

    @Override
    public List<TradingRecordedEvent> execute(List<OrderBookAsksDto> orderBookDto, MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        for (OrderBookAsksDto asks : orderBookDto) {
            Quantity execQuantity = new Quantity(BigDecimal.valueOf(asks.getAskSize()));
            Boolean result = tradingDomainService.applyTrade(marketOrder, execQuantity);
            if (!result) {
                break;
            }
            trades.add(
                    tradingDomainService.createMarketTrade(new TradeId(UUID.randomUUID()), marketOrder.getUserId(),
                            marketOrder.getId(), new OrderPrice(BigDecimal.valueOf(asks.getAskPrice())),
                            execQuantity, new CreatedAt(LocalDateTime.now()),
                            TransactionType.fromString(marketOrder.getOrderSide().getValue())));
            if (marketOrder.isFilled()) {
                break;
            }
        }
        return trades;
    }
}
