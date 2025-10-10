package shop.shportfolio.trading.application.ports.input.kafka.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.ports.input.kafka.LimitOrderCancelListener;
import shop.shportfolio.trading.application.saga.CancelOrderSaga;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

@Component
public class LimitOrderCancelListenerImpl implements LimitOrderCancelListener {

//    private final TradingUpdateHandler tradingUpdateHandler;
//    private final UserBalanceHandler userBalanceHandler;
//    @Autowired
//    public LimitOrderCancelListenerImpl(TradingUpdateHandler tradingUpdateHandler,
//                                        UserBalanceHandler userBalanceHandler) {
//        this.tradingUpdateHandler = tradingUpdateHandler;
//        this.userBalanceHandler = userBalanceHandler;
//    }
    private final CancelOrderSaga cancelOrderSaga;

    @Autowired
    public LimitOrderCancelListenerImpl(CancelOrderSaga cancelOrderSaga) {
        this.cancelOrderSaga = cancelOrderSaga;
    }

    @Override
    @Transactional
    public void cancelLimitOrder(CancelOrderDto cancelOrderDto) {

//        tradingUpdateHandler.cancelLimitOrder(cancelOrderDto.getOrderId(),
//                cancelOrderDto.getUserId());
//        userBalanceHandler.unlockBalance(cancelOrderDto.getUserId(), cancelOrderDto.getOrderId());
        cancelOrderSaga.cancelLimitOrder(cancelOrderDto);
    }

    @Override
    @Transactional
    public void revertLimitOrder(CancelOrderDto cancelOrderDto) {
//        tradingUpdateHandler.compensationCancelLimitOrder(cancelOrderDto.getOrderId(),
//                cancelOrderDto.getUserId());
//        userBalanceHandler.unlockBalance(cancelOrderDto.getUserId(), cancelOrderDto.getOrderId());
        cancelOrderSaga.revertLimitOrder(cancelOrderDto);
    }
}
