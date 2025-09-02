package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.PredicatedTradeAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class PredicatedTradeKafkaListener implements MessageHandler<PredicatedTradeAvroModel> {

    private final PredicatedTradeListener predicatedTradeListener;
    private final TradingMessageMapper tradingMessageMapper;
    @Autowired
    public PredicatedTradeKafkaListener(PredicatedTradeListener predicatedTradeListener,
                                        TradingMessageMapper tradingMessageMapper) {
        this.predicatedTradeListener = predicatedTradeListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.predicated.topic}")
    public void handle(List<PredicatedTradeAvroModel> messaging, List<String> key) {
        messaging.forEach(record -> {
            PredicatedTradeKafkaResponse response = tradingMessageMapper.toPredicatedTradeKafkaResponse(record);
            predicatedTradeListener.process(response);
        });
    }
}
