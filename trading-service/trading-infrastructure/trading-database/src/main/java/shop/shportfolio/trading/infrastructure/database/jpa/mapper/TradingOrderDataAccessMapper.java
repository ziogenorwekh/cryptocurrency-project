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
                .userId(new UserId(limitOrderEntity.getUserId()))
                .orderPrice(new OrderPrice(limitOrderEntity.getPrice()))
                .orderType(limitOrderEntity.getOrderType())
                .remainingQuantity(new Quantity(limitOrderEntity.getQuantity()))
                .orderSide(OrderSide.of(limitOrderEntity.getOrderSide()))
                .marketId(new MarketId(limitOrderEntity.getMarketId()))
                .quantity(new Quantity(limitOrderEntity.getQuantity()))
                .orderId(new OrderId(limitOrderEntity.getOrderId()))
                .build();

    }
}
