package shop.shportfolio.matching.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.ReservationOrderAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Component
public class ReservationOrderKafkaListener implements MessageHandler<ReservationOrderAvroModel> {

    private final CreatedOrderListener createdOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public ReservationOrderKafkaListener(CreatedOrderListener createdOrderListener,
                                         MatchingMessageMapper matchingMessageMapper) {
        this.createdOrderListener = createdOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    @KafkaListener(topics = "${kafka.reservationorder.topic}", groupId = "matching-group")
    public void handle(List<ReservationOrderAvroModel> messaging, List<String> key) {
        messaging.forEach(reservationOrderAvroModel -> {
            ReservationOrder reservationOrder = matchingMessageMapper
                    .reservationOrderToReservationOrderAvroModel(reservationOrderAvroModel);
            createdOrderListener.saveReservationOrder(reservationOrder);
        });
    }
}
