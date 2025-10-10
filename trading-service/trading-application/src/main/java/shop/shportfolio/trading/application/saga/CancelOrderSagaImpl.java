package shop.shportfolio.trading.application.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.LimitOrderCanceledEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCanceledEvent;

@Slf4j
@Component
public class CancelOrderSagaImpl implements CancelOrderSaga {

    private final TradingUpdateHandler tradingUpdateHandler;
    private final TradingTrackHandler tradingTrackHandler;
    private final UserBalanceHandler userBalanceHandler;

    @Autowired
    public CancelOrderSagaImpl(TradingUpdateHandler tradingUpdateHandler,
                               TradingTrackHandler tradingTrackHandler,
                               UserBalanceHandler userBalanceHandler) {
        this.tradingUpdateHandler = tradingUpdateHandler;
        this.tradingTrackHandler = tradingTrackHandler;
        this.userBalanceHandler = userBalanceHandler;
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

    @Override
    @Transactional
    public void cancelLimitOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.cancelLimitOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());
        userBalanceHandler.unlockBalance(cancelOrderDto.getUserId(), cancelOrderDto.getOrderId());
    }

    @Override
    @Transactional
    public void revertLimitOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.compensationCancelLimitOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());
//        userBalanceHandler.unlockBalance(cancelOrderDto.getUserId(), cancelOrderDto.getOrderId());
    }

    @Override
    @Transactional
    public void cancelReservationOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.cancelReservationOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());
        userBalanceHandler.unlockBalance(cancelOrderDto.getUserId(), cancelOrderDto.getOrderId());
    }

    @Override
    @Transactional
    public void revertReservationOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.compensationCancelReservationOrder(
                cancelOrderDto.getOrderId(), cancelOrderDto.getUserId()
        );
    }
}
