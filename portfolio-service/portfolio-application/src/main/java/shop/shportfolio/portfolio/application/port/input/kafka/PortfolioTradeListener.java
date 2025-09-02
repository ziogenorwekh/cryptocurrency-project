package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;

public interface PortfolioTradeListener {

    void handleTrade(TradeKafkaResponse response);
}
