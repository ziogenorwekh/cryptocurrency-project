package shop.shportfolio.trading.application.ports.input.kafka.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.ports.input.kafka.ReservationOrderCancelListener;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

@Component
public class ReservationOrderCancelListenerImpl implements ReservationOrderCancelListener {

    private final TradingUpdateHandler tradingUpdateHandler;

    @Autowired
    public ReservationOrderCancelListenerImpl(TradingUpdateHandler tradingUpdateHandler) {
        this.tradingUpdateHandler = tradingUpdateHandler;
    }


    @Override
    @Transactional
    public void cancelReservationOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.cancelReservationOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());

    }

    @Override
    @Transactional
    public void revertReservationOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.compensationCancelReservationOrder(
                cancelOrderDto.getOrderId(), cancelOrderDto.getUserId()
        );
    }
}
