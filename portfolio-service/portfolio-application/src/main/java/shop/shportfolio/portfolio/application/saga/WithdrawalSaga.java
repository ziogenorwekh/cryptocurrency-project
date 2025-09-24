package shop.shportfolio.portfolio.application.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.dto.WithdrawalResultContext;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;

@Component
public class WithdrawalSaga {

    private final PortfolioCreateHandler portfolioCreateHandler;
    private final PortfolioUpdateHandler portfolioUpdateHandler;
    private final WithdrawalPublisher withdrawalPublisher;

    @Autowired
    public WithdrawalSaga(PortfolioCreateHandler portfolioCreateHandler,
                          PortfolioUpdateHandler portfolioUpdateHandler,
                          WithdrawalPublisher withdrawalPublisher) {
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioUpdateHandler = portfolioUpdateHandler;
        this.withdrawalPublisher = withdrawalPublisher;
    }


    public DepositWithdrawal createWithdrawalSaga(WithdrawalCreateCommand withdrawalCreateCommand) {
        WithdrawalCreatedEvent withdrawalCreatedEvent = portfolioCreateHandler.withdrawal(withdrawalCreateCommand);
        withdrawalPublisher.publish(withdrawalCreatedEvent);
        return withdrawalCreatedEvent.getDomainType();
    }

    public DepositWithdrawal completeWithdrawalSaga(WithdrawalCreateCommand withdrawalCreateCommand) {
        return null;
    }
}
