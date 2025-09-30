package shop.shportfolio.trading.application.dto.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderContext {

    private final List<LimitOrder> limitOrders;
    private final List<ReservationOrder> reservationOrders;
    private final List<MarketOrder> marketOrders;
}
