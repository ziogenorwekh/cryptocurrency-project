package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface ReservationOrderExecutionUseCase {

    void executeReservationOrder(ReservationOrder reservationOrder);
}
