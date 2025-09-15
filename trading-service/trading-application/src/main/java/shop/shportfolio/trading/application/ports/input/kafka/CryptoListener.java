package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.crypto.CryptoKafkaResponse;

public interface CryptoListener {
    void updateCrypto(CryptoKafkaResponse response);
}
