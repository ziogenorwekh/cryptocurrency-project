package shop.shportfolio.trading.application.ports.input.kafka.impl;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.math.BigDecimal;

@Component
public class DepositWithdrawalListenerImpl implements DepositWithdrawalListener {

    private final UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    private final UserBalanceHandler userBalanceHandler;
    public DepositWithdrawalListenerImpl(
            UserBalanceKafkaPublisher userBalanceKafkaPublisher, UserBalanceHandler userBalanceHandler) {
        this.userBalanceKafkaPublisher = userBalanceKafkaPublisher;
        this.userBalanceHandler = userBalanceHandler;
    }

    @Override
    public void deposit(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));
        UserBalanceUpdatedEvent event = userBalanceHandler.deposit(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));
        userBalanceKafkaPublisher.publish(event);
    }

    @Override
    public void withdrawal(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));
        UserBalanceUpdatedEvent event = userBalanceHandler.withdraw(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));
        userBalanceKafkaPublisher.publish(event);
    }
}
