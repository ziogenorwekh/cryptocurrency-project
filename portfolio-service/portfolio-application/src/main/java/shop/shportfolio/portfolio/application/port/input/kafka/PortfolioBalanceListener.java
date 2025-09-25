package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;

public interface PortfolioBalanceListener {

    void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse);

}
