package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.handler.DepositWithdrawalTransactionHandler;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradingDepositWithdrawalPublisher;
import shop.shportfolio.trading.domain.event.DepositWithdrawalUpdatedEvent;

@Slf4j
@Component
public class DepositWithdrawalListenerImpl implements DepositWithdrawalListener {

    private final TradingDepositWithdrawalPublisher tradingDepositWithdrawalPublisher;
    private final DepositWithdrawalTransactionHandler depositWithdrawalTransactionHandler;
    public DepositWithdrawalListenerImpl(
            TradingDepositWithdrawalPublisher tradingDepositWithdrawalPublisher,
            DepositWithdrawalTransactionHandler depositWithdrawalTransactionHandler) {
        this.tradingDepositWithdrawalPublisher = tradingDepositWithdrawalPublisher;
        this.depositWithdrawalTransactionHandler = depositWithdrawalTransactionHandler;
    }

    @Override
    public void deposit(DepositWithdrawalKafkaResponse response) { // 👈 @Transactional 제거
        try {
            DepositWithdrawalUpdatedEvent eventToPublish = depositWithdrawalTransactionHandler.processDepositTransaction(response);
            tradingDepositWithdrawalPublisher.publish(eventToPublish);
        } catch (Exception e) {
            log.error("[Deposit] Failed to process deposit for user {}: {}", response.getUserId(), e.getMessage());
        }
    }

    @Override
    public void withdrawal(DepositWithdrawalKafkaResponse response) { // 👈 @Transactional 제거
        log.info("withdrawal response message -> {}", response.toString());
        DepositWithdrawalUpdatedEvent eventToPublish = depositWithdrawalTransactionHandler.processWithdrawalTransaction(response);
        tradingDepositWithdrawalPublisher.publish(eventToPublish);
        log.info("Finished withdrawal process for user {}", response.getUserId());
    }


}