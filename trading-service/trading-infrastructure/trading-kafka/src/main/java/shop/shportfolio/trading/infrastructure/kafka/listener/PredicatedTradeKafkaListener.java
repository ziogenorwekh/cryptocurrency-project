package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.PredicatedTradeAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeCreatedListener;

import java.util.List;

@Component
public class PredicatedTradeKafkaListener implements MessageHandler<PredicatedTradeAvroModel> {

    private final PredicatedTradeCreatedListener predicatedTradeCreatedListener;

    @Autowired
    public PredicatedTradeKafkaListener(PredicatedTradeCreatedListener predicatedTradeCreatedListener) {
        this.predicatedTradeCreatedListener = predicatedTradeCreatedListener;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.predicated.topic}")
    public void handle(List<PredicatedTradeAvroModel> messaging, List<String> key) {
        messaging.forEach(record -> {

        });
    }
}
