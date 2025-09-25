package shop.shportfolio.portfolio.application.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.dto.DepositWithdrawalKafkaResponse;
import shop.shportfolio.portfolio.application.dto.WithdrawalResultContext;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
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
    private final AssetChangeLogHandler assetChangeLogHandler;
    @Autowired
    public WithdrawalSaga(PortfolioCreateHandler portfolioCreateHandler,
                          PortfolioUpdateHandler portfolioUpdateHandler,
                          WithdrawalPublisher withdrawalPublisher,
                          AssetChangeLogHandler assetChangeLogHandler) {
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioUpdateHandler = portfolioUpdateHandler;
        this.withdrawalPublisher = withdrawalPublisher;
        this.assetChangeLogHandler = assetChangeLogHandler;
    }


    public DepositWithdrawal createWithdrawalSaga(WithdrawalCreateCommand withdrawalCreateCommand) {
        WithdrawalResultContext context = portfolioCreateHandler
                .withdrawal(withdrawalCreateCommand);
//        assetChangeLogHandler.saveWithdrawal(context.getWithdrawalCreatedEvent().getDomainType(),
//                context.getPortfolioId());
        withdrawalPublisher.publish(context.getWithdrawalCreatedEvent());
        return context.getWithdrawalCreatedEvent().getDomainType();
    }

    public void completeWithdrawalSaga(DepositWithdrawalKafkaResponse kafkaResponse) {
        portfolioUpdateHandler.completeWithdrawal(kafkaResponse);
    }

    public void failureWithdrawalSaga(DepositWithdrawalKafkaResponse kafkaResponse) {
        portfolioUpdateHandler.failWithdrawal(kafkaResponse);
    }
}
