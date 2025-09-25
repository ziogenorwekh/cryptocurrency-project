package shop.shportfolio.portfolio.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.portfolio.application.dto.DepositWithdrawalKafkaResponse;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioDepositWithdrawalListener;
import shop.shportfolio.portfolio.infrastructure.kafka.mapper.PortfolioMessageMapper;

import java.util.List;

@Component
public class PortfolioDepositWithdrawalKafkaListener implements MessageHandler<DepositWithdrawalAvroModel> {
    private final PortfolioDepositWithdrawalListener portfolioDepositWithdrawalListener;
    private final PortfolioMessageMapper portfolioMessageMapper;

    @Autowired
    public PortfolioDepositWithdrawalKafkaListener(
            PortfolioDepositWithdrawalListener portfolioDepositWithdrawalListener,
            PortfolioMessageMapper portfolioMessageMapper) {
        this.portfolioDepositWithdrawalListener = portfolioDepositWithdrawalListener;
        this.portfolioMessageMapper = portfolioMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "portfolio-group", topics = "${kafka.depositwithdrawal.event.topic}")
    public void handle(List<DepositWithdrawalAvroModel> messaging, List<String> key) {
        messaging.forEach(depositWithdrawalAvroModel -> {
            DepositWithdrawalKafkaResponse kafkaResponse = portfolioMessageMapper
                    .depositWithdrawalToDepositWithdrawalKafkaResponse(depositWithdrawalAvroModel);
            switch (depositWithdrawalAvroModel.getMessageType()) {
                case UPDATE -> {
                    if (depositWithdrawalAvroModel.getTransactionType() == TransactionType.DEPOSIT) {
                        portfolioDepositWithdrawalListener.handleDepositSuccess(kafkaResponse);
                    } else {
                        portfolioDepositWithdrawalListener.handleWithdrawalSuccess(kafkaResponse);
                    }
                }
                case FAIL -> {
                    portfolioDepositWithdrawalListener.handleWithdrawalFailure(kafkaResponse);
                }
            }
        });
    }
}
