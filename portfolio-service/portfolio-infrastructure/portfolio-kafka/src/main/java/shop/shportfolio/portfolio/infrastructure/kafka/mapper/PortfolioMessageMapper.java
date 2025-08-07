package shop.shportfolio.portfolio.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.DepositWithdrawalAvroModel;
import shop.shportfolio.common.avro.MessageType;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.avro.UserBalanceAvroModel;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class PortfolioMessageMapper {


    public DepositWithdrawalAvroModel depositWithdrawalToDepositWithdrawalAvroModel(
            DepositWithdrawal depositWithdrawal) {
        Instant instant = depositWithdrawal.getTransactionTime().getValue()
                .toInstant(ZoneOffset.UTC);

        return DepositWithdrawalAvroModel.newBuilder()
                .setAmount(depositWithdrawal.getAmount().getValue().longValue())
                .setMessageType(MessageType.CREATE)
                .setTransactionTime(instant)
                .setUserId(depositWithdrawal.getUserId().getValue().toString())
                .setTransactionType(toTransactionType(depositWithdrawal.getTransactionType()))
                .build();
    }

    public BalanceKafkaResponse userBalanceAvroModelToBalanceKafkaResponse(UserBalanceAvroModel model) {
        return new BalanceKafkaResponse(UUID.fromString(model.getUserId()),
                toAvroAssetCode(model.getAssetCode()),
                toTransactionType(model.getMessageType()),
                model.getTotalBalance());
    }

    private AssetCode toAvroAssetCode(shop.shportfolio.common.avro.AssetCode assetCode) {
        return AssetCode.fromString(assetCode.name());
    }

    private shop.shportfolio.common.domain.valueobject.MessageType toTransactionType(MessageType messageType) {
        return shop.shportfolio.common.domain.valueobject.MessageType.fromName(messageType.name());
    }

    private TransactionType toTransactionType(shop.shportfolio.common.domain.valueobject.
                                                      TransactionType value) {
        switch (value) {
            case DEPOSIT:
                return TransactionType.DEPOSIT;
            case WITHDRAWAL:
                return TransactionType.WITHDRAWAL;
            case TRADE_BUY:
                return TransactionType.TRADE_BUY;
            case TRADE_SELL:
                return TransactionType.TRADE_SELL;
        }
        throw new IllegalArgumentException("Invalid transaction type");
    }
}
