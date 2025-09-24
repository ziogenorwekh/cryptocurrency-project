package shop.shportfolio.matching.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MarketOrderAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.matching.infrastructure.kafka.mapper.MatchingMessageMapper;
import shop.shportfolio.trading.domain.entity.MarketOrder;

import java.util.List;
@Component
public class MarketOrderKafkaListener implements MessageHandler<MarketOrderAvroModel> {

    private final CreatedOrderListener createdOrderListener;
    private final MatchingMessageMapper matchingMessageMapper;

    @Autowired
    public MarketOrderKafkaListener(CreatedOrderListener createdOrderListener,
                                    MatchingMessageMapper matchingMessageMapper) {
        this.createdOrderListener = createdOrderListener;
        this.matchingMessageMapper = matchingMessageMapper;
    }

    @Override
    @KafkaListener(topics = "${kafka.marketorder.command.topic}", groupId = "matching-group")
    public void handle(List<MarketOrderAvroModel> messaging, List<String> key) {
        messaging.forEach(marketOrderAvroModel -> {
            MarketOrder marketOrder = matchingMessageMapper.marketOrderToMarketOrderAvroModel(marketOrderAvroModel);
            createdOrderListener.saveMarketOrder(marketOrder);
        });
    }
}
