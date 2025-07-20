package shop.shportfolio.trading.application.policy;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;

import static shop.shportfolio.trading.domain.valueobject.OrderSide.BUY;
import static shop.shportfolio.trading.domain.valueobject.OrderSide.SELL;

@Component
public class DefaultFeePolicy implements FeePolicy {
    private static final BigDecimal BUY_FEE_RATE = new BigDecimal("0.001");  // 0.1%
    private static final BigDecimal SELL_FEE_RATE = new BigDecimal("0.002"); // 0.2%

    @Override
    public FeeRate calculateDefualtFeeRate(OrderSide orderSide) {
        if (orderSide == null) {
            throw new IllegalArgumentException("OrderSide must not be null");
        }
        if (orderSide.equals(BUY)) {
            return new FeeRate(BUY_FEE_RATE);
        } else if (orderSide.equals(SELL)) {
            return new FeeRate(SELL_FEE_RATE);
        }
        throw new IllegalArgumentException("Unsupported OrderSide: " + orderSide);
    }

    @Override
    public FeeAmount calculateFeeAmount(OrderPrice orderPrice, FeeRate feeRate) {
        return FeeAmount.calculateFeeAmount(orderPrice, feeRate.getRate());
    }
}
