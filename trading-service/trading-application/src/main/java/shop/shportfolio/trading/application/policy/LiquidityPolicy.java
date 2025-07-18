package shop.shportfolio.trading.application.policy;

import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

import java.math.BigDecimal;

public interface LiquidityPolicy {

    BigDecimal calculateTotalAvailableSellQuantity(OrderBook orderBook);

    BigDecimal calculateTotalAvailableBuyQuantity(OrderBook orderBook);
}
