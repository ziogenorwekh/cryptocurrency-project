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
public class LimitOrderKafkaListener implements MessageHandler<LimitOrderAvroModel> {

    private final CreatedOrderListener createdOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public LimitOrderKafkaListener(CreatedOrderListener createdOrderListener,
                                   MatchingMessageMapper matchingMessageMapper) {
        this.createdOrderListener = createdOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    @KafkaListener(topics = "${kafka.limitorder.topic}", groupId = "matching-group")
    public void handle(List<LimitOrderAvroModel> messaging, List<String> key) {
        messaging.forEach(limitOrderAvroModel -> {
            LimitOrder limitOrder = matchingMessageMapper.limitOrderToLimitOrderAvroModel(limitOrderAvroModel);
            createdOrderListener.saveLimitOrder(limitOrder);
        });
    }
}
