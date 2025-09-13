package shop.shportfolio.matching.application.ports.input.kafka;

import shop.shportfolio.matching.application.dto.order.OrderCancelKafkaResponse;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface DeletedOrderListener {

    void deleteLimitOrder(OrderCancelKafkaResponse response);

    void deleteReservationOrder(OrderCancelKafkaResponse response);
}
