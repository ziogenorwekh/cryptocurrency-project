package shop.shportfolio.trading.application.policy;

import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;

public interface FeePolicy {
    FeeRate calculateDefualtFeeRate(OrderSide orderSide);

    FeeAmount calculateFeeAmount(OrderPrice orderPrice, FeeRate feeRate);
}
