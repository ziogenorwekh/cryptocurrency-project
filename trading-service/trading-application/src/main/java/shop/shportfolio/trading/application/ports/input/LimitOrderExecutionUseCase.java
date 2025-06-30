package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.domain.entity.LimitOrder;

public interface LimitOrderExecutionUseCase {

    public void executeLimitOrder(LimitOrder limitOrder);
}
