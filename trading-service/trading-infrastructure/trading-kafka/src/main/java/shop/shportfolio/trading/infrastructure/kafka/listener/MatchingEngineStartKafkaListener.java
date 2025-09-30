package shop.shportfolio.trading.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.MatchingEngineStartAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.ports.input.kafka.MatchingEngineStartListener;

import java.util.List;

@Slf4j
@Component
public class MatchingEngineStartKafkaListener implements MessageHandler<MatchingEngineStartAvroModel> {

    private final MatchingEngineStartListener matchingEngineStartListener;

    @Autowired
    public MatchingEngineStartKafkaListener(MatchingEngineStartListener matchingEngineStartListener) {
        this.matchingEngineStartListener = matchingEngineStartListener;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.matching.start.command.topic}")
    public void handle(List<MatchingEngineStartAvroModel> messaging, List<String> key) {
        messaging.forEach(matchingEngineStartAvroModel -> {
            log.info("matching engine start listener received -> {} ", matchingEngineStartAvroModel.toString());
            matchingEngineStartListener.sendOpenOrdersToMatchingEngine();
        });
    }
}
