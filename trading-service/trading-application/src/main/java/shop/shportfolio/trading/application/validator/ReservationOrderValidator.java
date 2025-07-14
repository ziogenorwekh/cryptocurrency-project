package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.policy.LiquidityPolicy;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Component
public class ReservationOrderValidator implements OrderValidator<ReservationOrder> {

    private final OrderBookManager orderBookManager;
    private final LiquidityPolicy liquidityPolicy;

    public ReservationOrderValidator(OrderBookManager orderBookManager,
                                     LiquidityPolicy liquidityPolicy) {
        this.orderBookManager = orderBookManager;
        this.liquidityPolicy = liquidityPolicy;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(ReservationOrder order, MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = liquidityPolicy.calculateTotalAvailableSellQuantity(orderBook);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Buy order quantity exceeds available sell liquidity.");
        }
    }

    @Override
    public void validateSellOrder(ReservationOrder order, MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = liquidityPolicy.calculateTotalAvailableBuyQuantity(orderBook);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Sell order quantity exceeds available buy liquidity.");
        }
    }


}
