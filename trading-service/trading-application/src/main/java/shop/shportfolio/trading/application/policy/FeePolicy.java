package shop.shportfolio.trading.application.policy;

import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.trading.domain.valueobject.OrderSide;

public interface FeePolicy {
    FeeRate calculateFeeRate(OrderSide orderSide);
}
