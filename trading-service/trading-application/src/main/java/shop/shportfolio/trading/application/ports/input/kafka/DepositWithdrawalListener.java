package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;

public interface DepositWithdrawalListener {

    void deposit(DepositWithdrawalKafkaResponse response);

    void withdrawal(DepositWithdrawalKafkaResponse response);
}
