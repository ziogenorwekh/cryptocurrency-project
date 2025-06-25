package shop.shportfolio.trading.application.handler.create.strategy.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
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
public class OrderExecutionMarketBids implements OrderExecutionStrategy<OrderBookBidsDto, MarketOrder> {


    private final TradingDomainService tradingDomainService;

    @Autowired
    public OrderExecutionMarketBids(TradingDomainService tradingDomainService) {
        this.tradingDomainService = tradingDomainService;
    }

    @Override
    public List<TradingRecordedEvent> execute(List<OrderBookBidsDto> orderBookDto, MarketOrder marketOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();
        for (OrderBookBidsDto bids : orderBookDto) {
            Quantity execQuantity = new Quantity(BigDecimal.valueOf(bids.getBidSize()));
            Boolean result = tradingDomainService.applyTrade(marketOrder, execQuantity);
            if (!result) {
                break;
            }
            trades.add(
                    tradingDomainService.createMarketTrade(new TradeId(UUID.randomUUID()), marketOrder.getUserId(),
                            marketOrder.getId(), new OrderPrice(BigDecimal.valueOf(bids.getBidPrice())),
                            execQuantity, new CreatedAt(LocalDateTime.now()),
                            TransactionType.fromString(marketOrder.getOrderSide().getValue())));
            if (marketOrder.isFilled()) {
                break;
            }
        }
        return trades;
    }
}
