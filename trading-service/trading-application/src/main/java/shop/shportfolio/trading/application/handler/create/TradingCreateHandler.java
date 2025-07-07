package shop.shportfolio.trading.application.handler.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateReservationOrderCommand;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.*;

@Slf4j
@Component
public class TradingCreateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    private final TradingDomainService tradingDomainService;



    @Autowired
    public TradingCreateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort,
                                TradingDomainService tradingDomainService) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
        this.tradingDomainService = tradingDomainService;
    }

    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(command.getMarketId())
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
        LimitOrder limitOrder = tradingDomainService.createLimitOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                new OrderPrice(command.getPrice())
                , OrderType.valueOf(command.getOrderType()));
        return tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
    }

    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketItem marketItem = findMarketItemByMarketId(command.getMarketId());
        return tradingDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()));
    }

    public ReservationOrder createReservationOrder(CreateReservationOrderCommand command) {
        MarketItem marketItem = findMarketItemByMarketId(command.getMarketId());
        ReservationOrder reservationOrder = tradingDomainService.createReservationOrder(
                new UserId(command.getUserId()), new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()), TriggerCondition.of(TriggerType.valueOf(command.getTriggerType()),
                        new OrderPrice(command.getTargetPrice())), ScheduledTime.of(command.getScheduledTime()),
                new ExpireAt(command.getExpireAt()), IsRepeatable.of(command.getIsRepeatable())
        );
        log.info("created Reservation Order ID: {}", reservationOrder.getId().getValue());
        return tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
    }


    private MarketItem findMarketItemByMarketId(String marketId) {
        return tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
    }
}