package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CancelOrderAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.OrderType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.order.CancelOrderDto;
import shop.shportfolio.trading.application.ports.input.kafka.LimitOrderCancelListener;
import shop.shportfolio.trading.application.ports.input.kafka.ReservationOrderCancelListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class OrderCancelKafkaListener implements MessageHandler<CancelOrderAvroModel> {

    private final LimitOrderCancelListener limitOrderCancelListener;
    private final ReservationOrderCancelListener reservationOrderCancelListener;
    private final TradingMessageMapper tradingMessageMapper;
    public OrderCancelKafkaListener(LimitOrderCancelListener limitOrderCancelListener,
                                    ReservationOrderCancelListener reservationOrderCancelListener, TradingMessageMapper tradingMessageMapper) {
        this.limitOrderCancelListener = limitOrderCancelListener;
        this.reservationOrderCancelListener = reservationOrderCancelListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.order.topic}")
    public void handle(List<CancelOrderAvroModel> messaging, List<String> key) {
        messaging.forEach(cancelOrderAvroModel -> {
            CancelOrderDto cancelOrderDto = tradingMessageMapper.toCancelOrderDto(cancelOrderAvroModel);
            if (cancelOrderAvroModel.getMessageType().equals(MessageType.DELETE)) {
                if (cancelOrderAvroModel.getOrderType().equals(OrderType.LIMIT)) {
                    limitOrderCancelListener.cancelLimitOrder(cancelOrderDto);
                } else if (cancelOrderAvroModel.getOrderType().equals(OrderType.RESERVATION)) {
                    reservationOrderCancelListener.cancelReservationOrder(cancelOrderDto);
                }
            } else if(cancelOrderAvroModel.getMessageType().equals(MessageType.FAIL)) {
                if (cancelOrderAvroModel.getOrderType().equals(OrderType.LIMIT)) {
                    limitOrderCancelListener.revertLimitOrder(cancelOrderDto);
                } else if (cancelOrderAvroModel.getOrderType().equals(OrderType.RESERVATION)) {
                    reservationOrderCancelListener.revertReservationOrder(cancelOrderDto);
                }
            }
        });
    }
}
