package shop.shportfolio.trading.application.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.ports.input.TradingUpdateUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

@Slf4j
@Component
public class TradingUpdateFacade implements TradingUpdateUseCase {

    private final TradingUpdateHandler tradingUpdateHandler;
    private final TradingTrackHandler tradingTrackHandler;

    @Autowired
    public TradingUpdateFacade(TradingUpdateHandler tradingUpdateHandler,
                               TradingTrackHandler tradingTrackHandler) {
        this.tradingUpdateHandler = tradingUpdateHandler;
        this.tradingTrackHandler = tradingTrackHandler;
    }
    @Override
    public LimitOrder cancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand) {
        LimitOrder limitOrder = tradingTrackHandler.findLimitOrderById(cancelLimitOrderCommand.getOrderId(),
                cancelLimitOrderCommand.getUserId());
        log.info("find limit order id {}", limitOrder.getId().getValue());
        return tradingUpdateHandler.cancelLimitOrder(limitOrder);
    }

    @Override
    public ReservationOrder cancelReservationOrder(
            CancelReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingTrackHandler
                .findReservationOrderByOrderIdAndUserId(command.getOrderId(), command.getUserId());
        return tradingUpdateHandler.cancelReservationOrder(reservationOrder);
    }
}
