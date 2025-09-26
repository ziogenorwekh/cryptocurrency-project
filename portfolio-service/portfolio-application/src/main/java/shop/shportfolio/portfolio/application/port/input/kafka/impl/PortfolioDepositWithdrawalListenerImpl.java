package shop.shportfolio.portfolio.application.port.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.dto.DepositWithdrawalKafkaResponse;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioDepositWithdrawalListener;
import shop.shportfolio.portfolio.application.saga.WithdrawalSaga;

@Slf4j
@Component
public class PortfolioDepositWithdrawalListenerImpl implements PortfolioDepositWithdrawalListener {

    private final WithdrawalSaga withdrawalSaga;
    private final PortfolioUpdateHandler portfolioUpdateHandler;

    public PortfolioDepositWithdrawalListenerImpl(WithdrawalSaga withdrawalSaga,
                                                  PortfolioUpdateHandler portfolioUpdateHandler) {
        this.withdrawalSaga = withdrawalSaga;
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    @Override
    @Transactional
    public void handleWithdrawalSuccess(DepositWithdrawalKafkaResponse kafkaResponse) {
        withdrawalSaga.completeWithdrawalSaga(kafkaResponse);
    }

    @Override
    @Transactional
    public void handleWithdrawalFailure(DepositWithdrawalKafkaResponse kafkaResponse) {
        withdrawalSaga.failureWithdrawalSaga(kafkaResponse);
    }

    @Override
    @Transactional
    public void handleDepositSuccess(DepositWithdrawalKafkaResponse kafkaResponse) {
        portfolioUpdateHandler.updateCurrencyBalance(new BalanceKafkaResponse(
                kafkaResponse.getUserId(),
                AssetCode.KRW,
                MessageType.UPDATE,
                kafkaResponse.getAmount()
        ));
    }
}
