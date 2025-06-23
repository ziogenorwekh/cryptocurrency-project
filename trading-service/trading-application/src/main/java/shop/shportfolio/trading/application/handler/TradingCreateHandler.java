package shop.shportfolio.trading.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.Quantity;

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
        return tradingRepositoryAdapter.save(limitOrder);
    }
}
