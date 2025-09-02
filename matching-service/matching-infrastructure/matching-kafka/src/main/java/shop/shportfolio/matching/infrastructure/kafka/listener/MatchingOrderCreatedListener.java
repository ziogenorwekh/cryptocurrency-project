package shop.shportfolio.matching.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.LimitOrderAvroModel;
import shop.shportfolio.common.avro.MarketOrderAvroModel;
import shop.shportfolio.common.avro.ReservationOrderAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Component
public class MatchingOrderCreatedListener{

    private final CreatedOrderListener createdOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public MatchingOrderCreatedListener(CreatedOrderListener createdOrderListener,
                                        MatchingMessageMapper matchingMessageMapper) {
        this.createdOrderListener = createdOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @KafkaListener(topics = "${kafka.trading.limitorder.created.matching.topic}", groupId = "matching-group")
    public void consumeLimitOrder(LimitOrderAvroModel avroModel) {
        LimitOrder limitOrder = matchingMessageMapper.limitOrderToLimitOrderAvroModel(avroModel);
        createdOrderListener.saveLimitOrder(limitOrder);
    }
    @KafkaListener(topics = "${kafka.trading.reservationorder.created.matching.topic}", groupId = "matching-group")
    public void consumeReservationOrder(ReservationOrderAvroModel avroModel) {
        ReservationOrder reservationOrder = matchingMessageMapper.reservationOrderToReservationOrderAvroModel(avroModel);
        createdOrderListener.saveReservationOrder(reservationOrder);
    }
    @KafkaListener(topics = "${kafka.trading.marketorder.created.matching.topic}", groupId = "matching-group")
    public void consumeMarketOrder(MarketOrderAvroModel avroModel) {
        MarketOrder marketOrder = matchingMessageMapper.marketOrderToMarketOrderAvroModel(avroModel);
        createdOrderListener.saveMarketOrder(marketOrder);
    }
}
