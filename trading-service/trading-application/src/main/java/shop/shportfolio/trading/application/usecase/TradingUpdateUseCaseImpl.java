package shop.shportfolio.trading.application.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.ports.input.TradingUpdateUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;

@Slf4j
@Component
public class TradingUpdateUseCaseImpl implements TradingUpdateUseCase {

    private final TradingUpdateHandler tradingUpdateHandler;
    private final TradingTrackHandler tradingTrackHandler;

    @Autowired
    public TradingUpdateUseCaseImpl(TradingUpdateHandler tradingUpdateHandler,
                                    TradingTrackHandler tradingTrackHandler) {
        this.tradingUpdateHandler = tradingUpdateHandler;
        this.tradingTrackHandler = tradingTrackHandler;
    }
    @Override
    @Transactional
    public LimitOrderCanceledEvent pendingCancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrder limitOrder = tradingTrackHandler.findLimitOrderById(cancelLimitOrderCommand.getOrderId(),
                cancelLimitOrderCommand.getUserId());
        LimitOrderCanceledEvent limitOrderCanceledEvent = tradingUpdateHandler.cancelPendingLimitOrder(limitOrder);
        log.info("find limit order id {}", limitOrder.getId().getValue());
        return limitOrderCanceledEvent;
    }

    @Override
    @Transactional
    public ReservationOrderCanceledEvent pendingCancelReservationOrder(
            CancelReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingTrackHandler
                .findReservationOrderByOrderIdAndUserId(command.getOrderId(), command.getUserId());
        log.info("find reservation order id {}", reservationOrder.getId().getValue());
        ReservationOrderCanceledEvent reservationOrderCanceledEvent = tradingUpdateHandler.cancelPendingReservationOrder(reservationOrder);
        return reservationOrderCanceledEvent;

    }
}
