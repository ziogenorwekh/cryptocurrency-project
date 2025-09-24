package shop.shportfolio.trading.infrastructure.kafka.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.kafka.listener.MessageHandler;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.infrastructure.kafka.mapper.TradingMessageMapper;

import java.util.List;

@Component
public class TradingDepositWithdrawalKafkaListener implements MessageHandler<DepositWithdrawalAvroModel> {

    private final DepositWithdrawalListener depositWithdrawalListener;
    private final TradingMessageMapper tradingMessageMapper;

    @Autowired
    public TradingDepositWithdrawalKafkaListener(DepositWithdrawalListener depositWithdrawalListener,
                                                 TradingMessageMapper tradingMessageMapper) {
        this.depositWithdrawalListener = depositWithdrawalListener;
        this.tradingMessageMapper = tradingMessageMapper;
    }

    @Override
    @KafkaListener(groupId = "trading-group", topics = "${kafka.depositwithdrawal.command.topic}")
    public void handle(List<DepositWithdrawalAvroModel> messaging, List<String> key) {
        messaging.forEach(depositWithdrawalModel -> {
            DepositWithdrawalKafkaResponse response = tradingMessageMapper
                    .depositWithdrawalAvroModelToDepositWithdrawalKafkaResponse(depositWithdrawalModel);
            switch (response.getTransactionType()) {
                case DEPOSIT:
                    depositWithdrawalListener.deposit(response);
                    break;
                case WITHDRAWAL:
                    depositWithdrawalListener.withdrawal(response);
                    break;
            }
        });
    }
}
