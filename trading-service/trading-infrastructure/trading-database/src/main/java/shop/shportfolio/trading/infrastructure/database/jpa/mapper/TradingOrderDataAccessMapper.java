package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.JpaTriggerCondition;

@Component
public class TradingOrderDataAccessMapper {

    public LimitOrder limitOrderEntityToLimitOrder(LimitOrderEntity limitOrderEntity) {
        return LimitOrder.builder()
                .orderId(new OrderId(limitOrderEntity.getOrderId()))
                .userId(new UserId(limitOrderEntity.getUserId()))
                .marketId(new MarketId(limitOrderEntity.getMarketId()))
                .orderSide(OrderSide.of(limitOrderEntity.getOrderSide()))
                .quantity(new Quantity(limitOrderEntity.getQuantity()))
                .orderPrice(new OrderPrice(limitOrderEntity.getPrice()))
                .remainingQuantity(new Quantity(limitOrderEntity.getQuantity()))
                .orderType(limitOrderEntity.getOrderType())
                .orderStatus(limitOrderEntity.getOrderStatus())
                .createdAt(new CreatedAt(limitOrderEntity.getCreatedAt()))
                .build();
    }

    public LimitOrderEntity limitOrderEntityToLimitOrderEntity(LimitOrder limitOrder) {
        return LimitOrderEntity.builder()
                .orderId(limitOrder.getId().getValue())
                .userId(limitOrder.getUserId().getValue())
                .marketId(limitOrder.getMarketId().getValue())
                .orderSide(limitOrder.getOrderSide().getValue())
                .quantity(limitOrder.getQuantity().getValue())
                .price(limitOrder.getOrderPrice().getValue())
                .remainingQuantity(limitOrder.getRemainingQuantity().getValue())
                .orderType(limitOrder.getOrderType())
                .orderStatus(limitOrder.getOrderStatus())
                .createdAt(limitOrder.getCreatedAt().getValue())
                .build();
    }

    public ReservationOrder reservationOrderEntityToReservationOrder(ReservationOrderEntity reservationOrderEntity) {
        return ReservationOrder.builder()
                .orderId(new OrderId(reservationOrderEntity.getOrderId()))
                .userId(new UserId(reservationOrderEntity.getUserId()))
                .marketId(new MarketId(reservationOrderEntity.getMarketId()))
                .orderSide(OrderSide.of(reservationOrderEntity.getOrderSide()))
                .quantity(new Quantity(reservationOrderEntity.getQuantity()))
                .orderType(reservationOrderEntity.getOrderType())
                .orderStatus(reservationOrderEntity.getOrderStatus())
                .createdAt(new CreatedAt(reservationOrderEntity.getCreatedAt()))
                .remainingQuantity(new Quantity(reservationOrderEntity.getRemainingQuantity()))
                .orderPrice(new OrderPrice(reservationOrderEntity.getPrice()))
                .expireAt(new ExpireAt(reservationOrderEntity.getExpireAt()))
                .isRepeatable(new IsRepeatable(reservationOrderEntity.getIsRepeatable()))
                .triggerCondition(jpaTriggerConditionToTriggerCondition(reservationOrderEntity.getJpaTriggerCondition()))
                .scheduledTime(new ScheduledTime(reservationOrderEntity.getScheduledTime()))
                .build();
    }

    public ReservationOrderEntity reservationOrderToReservationOrderEntity(ReservationOrder reservationOrder) {
        return ReservationOrderEntity.builder()
                .orderId(reservationOrder.getId().getValue())
                .userId(reservationOrder.getUserId().getValue())
                .marketId(reservationOrder.getMarketId().getValue())
                .orderSide(reservationOrder.getOrderSide().getValue())
                .quantity(reservationOrder.getQuantity().getValue())
                .price(reservationOrder.getOrderPrice().getValue())
                .remainingQuantity(reservationOrder.getRemainingQuantity().getValue())
                .orderType(reservationOrder.getOrderType())
                .orderStatus(reservationOrder.getOrderStatus())
                .createdAt(reservationOrder.getCreatedAt().getValue())
                .expireAt(reservationOrder.getExpireAt().getValue())
                .isRepeatable(reservationOrder.getIsRepeatable().isRepeatable())
                .triggerCondition(triggerConditionToJpaTriggerCondition(reservationOrder.getTriggerCondition()))
                .scheduledTime(reservationOrder.getScheduledTime().getValue())
                .build();
    }

    public MarketOrderEntity marketOrderEntityToMarketOrder(MarketOrder marketOrder) {
        return MarketOrderEntity.builder()
                .orderId(marketOrder.getId().getValue())
                .userId(marketOrder.getUserId().getValue())
                .marketId(marketOrder.getMarketId().getValue())
                .orderSide(marketOrder.getOrderSide().getValue())
                .quantity(marketOrder.getQuantity().getValue())
                .remainingQuantity(marketOrder.getRemainingQuantity().getValue())
                .orderType(marketOrder.getOrderType())
                .price(marketOrder.getOrderPrice().getValue())
                .orderStatus(marketOrder.getOrderStatus())
                .createdAt(marketOrder.getCreatedAt().getValue())
                .remainingPrice(marketOrder.getRemainingPrice().getValue())
                .build();
    }

    public MarketOrder marketOrderToMarketOrderEntity(MarketOrderEntity marketOrder) {
        return MarketOrder.builder()
                .orderId(new OrderId(marketOrder.getOrderId()))
                .userId(new UserId(marketOrder.getUserId()))
                .marketId(new MarketId(marketOrder.getMarketId()))
                .orderSide(OrderSide.of(marketOrder.getOrderSide()))
                .quantity(new Quantity(marketOrder.getQuantity()))
                .remainingQuantity(new Quantity(marketOrder.getRemainingQuantity()))
                .orderType(marketOrder.getOrderType())
                .orderStatus(marketOrder.getOrderStatus())
                .createdAt(new CreatedAt(marketOrder.getCreatedAt()))
                .remainingPrice(new OrderPrice(marketOrder.getRemainingPrice()))
                .build();
    }

    private TriggerCondition jpaTriggerConditionToTriggerCondition(JpaTriggerCondition triggerCondition) {
        return new TriggerCondition(triggerCondition.getTriggerType(),
                new OrderPrice(triggerCondition.getTargetPrice()));
    }

    private JpaTriggerCondition triggerConditionToJpaTriggerCondition(TriggerCondition triggerCondition) {
        return new JpaTriggerCondition(triggerCondition.getValue(),triggerCondition.getTargetPrice().getValue());
    }
}
