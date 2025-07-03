package shop.shportfolio.trading.application.handler.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.*;

@Slf4j
@Component
public class TradingCreateHandler {

    private final TradingRepositoryPort tradingRepositoryPort;
    private final TradingDomainService tradingDomainService;


    @Autowired
    public TradingCreateHandler(TradingRepositoryPort tradingRepositoryPort,
                                TradingDomainService tradingDomainService) {
        this.tradingRepositoryPort = tradingRepositoryPort;
        this.tradingDomainService = tradingDomainService;
    }

    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        MarketItem marketItem = tradingRepositoryPort
                .findMarketItemByMarketId(command.getMarketId())
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
        LimitOrder limitOrder = tradingDomainService.createLimitOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                new OrderPrice(command.getPrice())
                , OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryPort.saveLimitOrder(limitOrder);
    }

    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketItem marketItem = findMarketItemByMarketId(command.getMarketId());
        return tradingDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()));
    }


    private MarketItem findMarketItemByMarketId(String marketId) {
        return tradingRepositoryPort
                .findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
    }
}