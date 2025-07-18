package shop.shportfolio.trading.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;

@Component
public class DefaultLiquidityPolicy implements LiquidityPolicy {
    @Override
    public BigDecimal calculateTotalAvailableSellQuantity(OrderBook orderBook) {
        return orderBook.getSellPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateTotalAvailableBuyQuantity(OrderBook orderBook) {
        return orderBook.getBuyPriceLevels()
                .values()
                .stream()
                .flatMap(priceLevel -> priceLevel.getOrders().stream())
                .map(orderInBook -> orderInBook.getRemainingQuantity().getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
