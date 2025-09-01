package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;

public interface PredicatedTradeCreatedListener {

    void updateLimitOrder(PredicatedTradeKafkaResponse response);

    void updateReservationOrder(PredicatedTradeKafkaResponse response);

    void updateMarketOrder(PredicatedTradeKafkaResponse response);
}
