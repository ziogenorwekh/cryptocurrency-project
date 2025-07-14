package shop.shportfolio.trading.application.policy;

import shop.shportfolio.common.domain.valueobject.OrderPrice;

import java.math.BigDecimal;

public interface PriceLimitPolicy {

    boolean isOverTenPercentHigher(OrderPrice price, BigDecimal reference);
    boolean isOverTenPercentLower(OrderPrice price, BigDecimal reference);
}
