package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;

@Component
public class TradingOrderDataAccessMapper {

    public LimitOrder limitOrderEntityToLimitOrder(LimitOrderEntity limitOrderEntity) {
        // 이거 완성 못함
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
}
