package shop.shportfolio.portfolio.application.port.input.kafka;

import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.dto.DepositWithdrawalKafkaResponse;

public interface PortfolioDepositWithdrawalListener {

    void handleWithdrawalSuccess(DepositWithdrawalKafkaResponse kafkaResponse);

    void handleWithdrawalFailure(DepositWithdrawalKafkaResponse kafkaResponse);

    void handleDepositSuccess(DepositWithdrawalKafkaResponse kafkaResponse);
}
