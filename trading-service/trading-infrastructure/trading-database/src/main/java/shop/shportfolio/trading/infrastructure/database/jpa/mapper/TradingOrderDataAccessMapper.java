package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.OrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.ReservationOrderEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.JpaTriggerCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


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
        boolean isBuy = marketOrder.getOrderSide().isBuy();

        BigDecimal price;
        BigDecimal quantity;
        BigDecimal remainingPrice;
        BigDecimal remainingQuantity;
        if (isBuy) {
            price = marketOrder.getOrderPrice().getValue();
            remainingPrice = marketOrder.getRemainingPrice().getValue();
            quantity = null;
            remainingQuantity = null;
        } else {
            price = null;
            remainingPrice = null;
            quantity = marketOrder.getQuantity().getValue();
            remainingQuantity = marketOrder.getRemainingQuantity().getValue();
        }

        return MarketOrderEntity.builder()
                .orderId(marketOrder.getId().getValue())
                .userId(marketOrder.getUserId().getValue())
                .marketId(marketOrder.getMarketId().getValue())
                .orderSide(marketOrder.getOrderSide().getValue())
                .orderType(marketOrder.getOrderType())
                .price(price)
                .quantity(quantity)
                .remainingPrice(remainingPrice)
                .remainingQuantity(remainingQuantity)
                .orderStatus(marketOrder.getOrderStatus())
                .createdAt(marketOrder.getCreatedAt() != null ? marketOrder.getCreatedAt().getValue() : LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }


    public MarketOrder marketOrderToMarketOrderEntity(MarketOrderEntity entity) {
        boolean isBuy = OrderSide.of(entity.getOrderSide()).isBuy();

        OrderPrice orderPrice = isBuy
                ? (entity.getRemainingPrice() != null ? new OrderPrice(entity.getRemainingPrice()) : null)
                : null;

        Quantity quantity = !isBuy && entity.getQuantity() != null
                ? new Quantity(entity.getQuantity())
                : null;

        Quantity remainingQuantity = !isBuy && entity.getRemainingQuantity() != null
                ? new Quantity(entity.getRemainingQuantity())
                : null;

        OrderPrice remainingPrice = isBuy
                ? (entity.getRemainingPrice() != null ? new OrderPrice(entity.getRemainingPrice()) : null)
                : null;

        return MarketOrder.builder()
                .orderId(new OrderId(entity.getOrderId()))
                .userId(new UserId(entity.getUserId()))
                .marketId(new MarketId(entity.getMarketId()))
                .orderSide(OrderSide.of(entity.getOrderSide()))
                .orderType(entity.getOrderType())
                .orderStatus(entity.getOrderStatus())
                .createdAt(new CreatedAt(entity.getCreatedAt()))
                .orderPrice(orderPrice)
                .quantity(quantity)
                .remainingQuantity(remainingQuantity)
                .remainingPrice(remainingPrice)
                .build();
    }

    public Order orderEntityToOrder(OrderEntity entity) {
        if (entity instanceof LimitOrderEntity limitOrderEntity) {
            return limitOrderEntityToLimitOrder(limitOrderEntity);
        } else if (entity instanceof MarketOrderEntity marketOrderEntity) {
            return marketOrderToMarketOrderEntity(marketOrderEntity);
        } else if (entity instanceof ReservationOrderEntity reservationOrderEntity) {
            return reservationOrderEntityToReservationOrder(reservationOrderEntity);
        } else {
            throw new IllegalArgumentException("Unknown OrderEntity type: " + entity.getClass().getName());
        }
    }

    private TriggerCondition jpaTriggerConditionToTriggerCondition(JpaTriggerCondition triggerCondition) {
        return new TriggerCondition(triggerCondition.getTriggerType(),
                new OrderPrice(triggerCondition.getTargetPrice()));
    }

    private JpaTriggerCondition triggerConditionToJpaTriggerCondition(TriggerCondition triggerCondition) {
        return new JpaTriggerCondition(triggerCondition.getValue(),triggerCondition.getTargetPrice().getValue());
    }
}
