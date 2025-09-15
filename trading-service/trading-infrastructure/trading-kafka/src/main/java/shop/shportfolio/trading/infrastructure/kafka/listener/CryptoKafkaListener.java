package shop.shportfolio.trading.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.CryptoAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.crypto.CryptoKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.CryptoListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Slf4j
@Component
public class CryptoKafkaListener implements MessageHandler<CryptoAvroModel> {

    private final CryptoListener cryptoListener;
    private final TradingMessageMapper tradingMessageMapper;

    public CryptoKafkaListener(CryptoListener cryptoListener, TradingMessageMapper tradingMessageMapper) {
        this.cryptoListener = cryptoListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.crypto.topic}")
    public void handle(List<CryptoAvroModel> messaging, List<String> key) {
        messaging.forEach(cryptoAvroModel -> {
            log.info("crypto listener received -> {} ",cryptoAvroModel.toString());
            CryptoKafkaResponse cryptoKafkaResponse = tradingMessageMapper.toCryptoKafkaResponse(cryptoAvroModel);
            if (cryptoAvroModel.getMessageType().equals(MessageType.UPDATE)) {
                cryptoListener.updateCrypto(cryptoKafkaResponse);
            }
        });
    }
}
