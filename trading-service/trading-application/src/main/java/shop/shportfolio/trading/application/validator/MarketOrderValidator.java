package shop.shportfolio.trading.application.validator;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

@Component
public class MarketOrderValidator implements OrderValidator<MarketOrder> {


    private final OrderBookManager orderBookManager;

    public MarketOrderValidator(OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public void validateBuyOrder(MarketOrder order,MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getSellPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Buy order quantity exceeds available sell liquidity.");
        }
    }

    @Override
    public void validateSellOrder(MarketOrder order,MarketItem marketItem) {
        OrderBook orderBook = orderBookManager
                .loadAdjustedOrderBook(marketItem.getId().getValue(), marketItem.getTickPrice().getValue());

        BigDecimal totalAvailableQty = orderBook.getBuyPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (order.getQuantity().getValue().compareTo(totalAvailableQty) > 0) {
            throw new OrderInValidatedException("Sell order quantity exceeds available buy liquidity.");
        }
    }
}
