package shop.shportfolio.trading.application.validator;

import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public class MarketOrderValidator implements OrderValidator<ReservationOrder> {


    @Override
    public boolean validateBuyOrder(ReservationOrder order) {
        return false;
    }

    @Override
    public boolean validateSellOrder(ReservationOrder order) {
        return false;
    }
}
