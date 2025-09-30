package shop.shportfolio.trading.application.handler.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateReservationOrderCommand;
import shop.shportfolio.trading.application.dto.context.OrderCreationContext;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;

@Slf4j
@Component
public class TradingCreateHandler {

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    private final OrderDomainService orderDomainService;



    @Autowired
    public TradingCreateHandler(TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort,
                                OrderDomainService orderDomainService) {
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
        this.orderDomainService = orderDomainService;
    }

    @Transactional
    public OrderCreationContext<LimitOrderCreatedEvent> createLimitOrder(CreateLimitOrderCommand command) {
        MarketItem marketItem = tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(command.getMarketId())
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
        LimitOrderCreatedEvent limitOrderCreatedEvent = orderDomainService.createLimitOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                new OrderPrice(command.getOrderPrice())
                , OrderType.valueOf(command.getOrderType()));
        tradingOrderRepositoryPort.saveLimitOrder(limitOrderCreatedEvent.getDomainType());
        return OrderCreationContext.<LimitOrderCreatedEvent>builder().domainEvent(limitOrderCreatedEvent)
                .marketItem(marketItem).build();
    }

    @Transactional
    public OrderCreationContext<MarketOrderCreatedEvent> createMarketOrder(CreateMarketOrderCommand command) {
        MarketItem marketItem = findMarketItemByMarketId(command.getMarketId());
        if (command.getQuantity() == null) {
            command.setQuantity(BigDecimal.ZERO);
        }
        MarketOrderCreatedEvent marketOrderCreatedEvent = orderDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                new OrderPrice(command.getOrderPrice()),
                OrderType.valueOf(command.getOrderType()));
        tradingOrderRepositoryPort.saveMarketOrder(marketOrderCreatedEvent.getDomainType());
        return OrderCreationContext.<MarketOrderCreatedEvent>builder().domainEvent(marketOrderCreatedEvent)
                .marketItem(marketItem).build();
    }

    @Transactional
    public OrderCreationContext<ReservationOrderCreatedEvent> createReservationOrder(CreateReservationOrderCommand command) {
        MarketItem marketItem = findMarketItemByMarketId(command.getMarketId());
        ReservationOrderCreatedEvent reservationOrderCreatedEvent = orderDomainService.createReservationOrder(
                new UserId(command.getUserId()), new MarketId(marketItem.getId().getValue()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()), TriggerCondition.of(TriggerType.valueOf(command.getTriggerType()),
                        new OrderPrice(command.getTargetPrice())), ScheduledTime.of(command.getScheduledTime()),
                new ExpireAt(command.getExpireAt()), IsRepeatable.of(command.getIsRepeatable())
        );
        log.info("created Reservation Order ID: {}", reservationOrderCreatedEvent.getDomainType().getId().getValue());
        tradingOrderRepositoryPort.saveReservationOrder(reservationOrderCreatedEvent.getDomainType());
        return OrderCreationContext.<ReservationOrderCreatedEvent>builder()
                .domainEvent(reservationOrderCreatedEvent).marketItem(marketItem).build();
    }



    private MarketItem findMarketItemByMarketId(String marketId) {
        return tradingMarketDataRepositoryPort
                .findMarketItemByMarketId(marketId)
                .orElseThrow(() -> new MarketItemNotFoundException("marketId not found"));
    }
}