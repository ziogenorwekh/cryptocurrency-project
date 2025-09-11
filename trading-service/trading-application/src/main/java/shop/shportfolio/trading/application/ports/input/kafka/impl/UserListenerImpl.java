package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.input.kafka.UserListener;

import java.util.UUID;

@Slf4j
@Component
public class UserListenerImpl implements UserListener {

    private final UserBalanceHandler userBalanceHandler;

    @Autowired
    public UserListenerImpl(UserBalanceHandler userBalanceHandler) {
        this.userBalanceHandler = userBalanceHandler;
    }

    @Override
    @Transactional
    public void createUserBalance(UUID userId) {
        log.info("Creating user balance for userId: {}", userId);
        userBalanceHandler.createUserBalance(userId);
    }

    @Override
    @Transactional
    public void deleteUserBalance(UUID userId) {
        log.info("Deleting user balance for userId: {}", userId);
        userBalanceHandler.deleteUserBalance(userId);
    }
}
