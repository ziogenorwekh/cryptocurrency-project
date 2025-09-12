package shop.shportfolio.trading.application.ports.input.kafka.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.ports.input.kafka.LimitOrderCancelListener;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

@Component
public class LimitOrderCancelListenerImpl implements LimitOrderCancelListener {

    private final TradingUpdateHandler tradingUpdateHandler;

    @Autowired
    public LimitOrderCancelListenerImpl(TradingUpdateHandler tradingUpdateHandler) {
        this.tradingUpdateHandler = tradingUpdateHandler;
    }

    @Override
    @Transactional
    public void cancelLimitOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.cancelLimitOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());
    }

    @Override
    @Transactional
    public void revertLimitOrder(CancelOrderDto cancelOrderDto) {
        tradingUpdateHandler.compensationCancelLimitOrder(cancelOrderDto.getOrderId(),
                cancelOrderDto.getUserId());
    }
}
