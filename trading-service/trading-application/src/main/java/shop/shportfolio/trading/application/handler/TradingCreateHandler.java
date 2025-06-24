package shop.shportfolio.trading.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Trade;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TradingCreateHandler {

    private final TradingRepositoryAdapter tradingRepositoryAdapter;
    private final TradingDomainService tradingDomainService;

    @Autowired
    public TradingCreateHandler(TradingRepositoryAdapter tradingRepositoryAdapter,
                                TradingDomainService tradingDomainService) {
        this.tradingRepositoryAdapter = tradingRepositoryAdapter;
        this.tradingDomainService = tradingDomainService;
    }

    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingDomainService.createLimitOrder(new UserId(command.getUserId()), new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()), new OrderPrice(command.getPrice())
                , OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveLimitOrder(limitOrder);
    }

    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketOrder marketOrder = tradingDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveMarketOrder(marketOrder);
    }


    public List<Trade> execMarketOrder(List<OrderBookAsksDto> orderBookAsksDto, MarketOrder marketOrder) {
        List<Trade> trades = new ArrayList<>();
        for (OrderBookAsksDto asks : orderBookAsksDto) {
            Quantity execQuantity = new Quantity(BigDecimal.valueOf(asks.getAskSize()));
            Boolean result = tradingDomainService.applyTrade(marketOrder, execQuantity);
            if (!result) {
                break;
            }
            trades.add(tradingDomainService.createMarketTrade(new TradeId(UUID.randomUUID()), marketOrder.getUserId(),
                    marketOrder.getId(), new OrderPrice(BigDecimal.valueOf(asks.getAskPrice())),
                    execQuantity, new CreatedAt(LocalDateTime.now())));
            if (marketOrder.isFilled()) {
                break;
            }
        }
        return trades;
    }
}
