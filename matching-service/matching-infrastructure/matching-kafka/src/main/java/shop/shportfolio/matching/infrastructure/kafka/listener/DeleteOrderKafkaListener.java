package shop.shportfolio.matching.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CancelOrderAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.matching.application.dto.order.OrderCancelKafkaResponse;
import shop.shportfolio.matching.application.exception.MatchingApplicationException;
import shop.shportfolio.matching.application.ports.input.kafka.DeletedOrderListener;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;

import java.util.List;

@Component
public class DeleteOrderKafkaListener implements MessageHandler<CancelOrderAvroModel> {

    private final DeletedOrderListener deletedOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public DeleteOrderKafkaListener(DeletedOrderListener deletedOrderListener,
                                    MatchingMessageMapper matchingMessageMapper) {
        this.deletedOrderListener = deletedOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    public void handle(List<CancelOrderAvroModel> messaging, List<String> key) {
        messaging.forEach(cancelOrderAvroModel -> {
            OrderCancelKafkaResponse response = matchingMessageMapper
                    .cancelOrderAvroModelToOrderCancelKafkaResponse(cancelOrderAvroModel);
            switch (response.getOrderType()) {
                case LIMIT -> deletedOrderListener.deleteLimitOrder(response);
                case RESERVATION -> deletedOrderListener.deleteReservationOrder(response);
                default -> throw new MatchingApplicationException("Unknown order type: " + response.getOrderType());
            }
        });
    }
}
