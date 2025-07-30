package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;

public interface PortfolioBalanceKafkaListener {

    void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse);
}
