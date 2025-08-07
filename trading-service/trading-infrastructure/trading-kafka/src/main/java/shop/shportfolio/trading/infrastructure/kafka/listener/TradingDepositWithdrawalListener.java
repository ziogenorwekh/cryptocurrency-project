package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

public class TradingDepositWithdrawalListener implements MessageHandler<DepositWithdrawalAvroModel> {

    private final DepositWithdrawalListener depositWithdrawalListener;
    private final TradingMessageMapper tradingMessageMapper;

    public TradingDepositWithdrawalListener(DepositWithdrawalListener depositWithdrawalListener,
                                            TradingMessageMapper tradingMessageMapper) {
        this.depositWithdrawalListener = depositWithdrawalListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-listener-group", topics = "${kafka.portfolio.depositwithdrawal.trading.topic}")
    public void handle(List<DepositWithdrawalAvroModel> messaging, List<String> key) {
        messaging.forEach(depositWithdrawalModel -> {
            DepositWithdrawalKafkaResponse response = tradingMessageMapper
                    .depositWithdrawalAvroModelToDepositWithdrawalKafkaResponse(depositWithdrawalModel);
            if(response.getTransactionType() == TransactionType.DEPOSIT) {
                depositWithdrawalListener.deposit(response);
            } else if(response.getTransactionType() == TransactionType.WITHDRAWAL) {
                depositWithdrawalListener.withdrawal(response);
            }
        });
    }
}
