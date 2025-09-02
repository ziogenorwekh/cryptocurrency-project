package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;

public interface PredicatedTradeListener {

    void process(PredicatedTradeKafkaResponse response);
}
