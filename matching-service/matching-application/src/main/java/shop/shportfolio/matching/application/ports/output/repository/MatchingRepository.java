package shop.shportfolio.matching.application.ports.output.repository;

import shop.shportfolio.matching.application.dto.order.OrderContext;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

public interface MatchingRepository {

    OrderContext findAllOrders();
}
