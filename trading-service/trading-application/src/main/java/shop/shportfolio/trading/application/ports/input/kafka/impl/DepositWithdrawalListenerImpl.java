package shop.shportfolio.trading.application.ports.input.kafka.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalancePublisher;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.math.BigDecimal;

@Component
public class DepositWithdrawalListenerImpl implements DepositWithdrawalListener {

    private final UserBalancePublisher userBalancePublisher;
    private final UserBalanceHandler userBalanceHandler;

    public DepositWithdrawalListenerImpl(
            UserBalancePublisher userBalancePublisher, UserBalanceHandler userBalanceHandler) {
        this.userBalancePublisher = userBalancePublisher;
        this.userBalanceHandler = userBalanceHandler;
    }

    @Override
    @Transactional
    public void deposit(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserOptionalBalanceByUserId(response.getUserId())
                .orElseGet(() -> userBalanceHandler.createUserBalance(response.getUserId()));
        UserBalanceUpdatedEvent event = userBalanceHandler.deposit(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));
        userBalancePublisher.publish(event);
    }


    @Override
    @Transactional
    public void withdrawal(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));
        UserBalanceUpdatedEvent event = userBalanceHandler.withdraw(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));
        userBalancePublisher.publish(event);
    }
}
