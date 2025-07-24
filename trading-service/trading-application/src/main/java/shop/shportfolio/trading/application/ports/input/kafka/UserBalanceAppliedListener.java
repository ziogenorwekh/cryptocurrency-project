package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.userbalance.UserBalanceKafkaResponse;

public interface UserBalanceAppliedListener {

    void receiveUserBalance(UserBalanceKafkaResponse userBalanceKafkaResponse);
}
