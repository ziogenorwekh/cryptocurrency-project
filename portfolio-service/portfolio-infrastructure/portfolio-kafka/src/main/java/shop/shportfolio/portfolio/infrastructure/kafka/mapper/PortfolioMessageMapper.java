package shop.shportfolio.portfolio.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.*;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.view.CryptoView;

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
                .setTransactionType(toAvroTransactionType(depositWithdrawal.getTransactionType()))
                .build();
    }

    public TradeKafkaResponse tradeToTradeKafkaResponse(TradeAvroModel tradeAvroModel) {
        return new TradeKafkaResponse(tradeAvroModel.getTradeId(),
                tradeAvroModel.getMarketId(),
                tradeAvroModel.getUserId(),
                tradeAvroModel.getBuyOrderId(),
                tradeAvroModel.getSellOrderId(),
                tradeAvroModel.getOrderPrice(),
                tradeAvroModel.getQuantity(),
                toDomainTransactionType(tradeAvroModel.getTransactionType()),
                tradeAvroModel.getCreatedAt());
    }

    public BalanceKafkaResponse userBalanceAvroModelToBalanceKafkaResponse(UserBalanceAvroModel model) {
        return new BalanceKafkaResponse(UUID.fromString(model.getUserId()),
                toAvroAssetCode(model.getAssetCode()),
                toDomainMessageType(model.getMessageType()),
                model.getTotalBalance());
    }

    public CryptoAvroModel toCryptoAvroModel(CryptoView cryptoView,
                                             shop.shportfolio.common.domain.valueobject.MessageType messageType) {

        return CryptoAvroModel.newBuilder()
                .setBalanceId(cryptoView.getId().getValue().toString())
                .setUserId(cryptoView.getUserId().getValue().toString())
                .setMarketId(cryptoView.getMarketId().getValue())
                .setQuantity(cryptoView.getQuantity().getValue().toString())
                .setPurchasePrice(cryptoView.getPurchasePrice().getValue().toString())
                .setMessageType(toAvroMessageType(messageType))
                .build();
    }

    private AssetCode toAvroAssetCode(shop.shportfolio.common.avro.AssetCode assetCode) {
        return AssetCode.fromString(assetCode.name());
    }

    private MessageType toAvroMessageType(shop.shportfolio.common.domain.valueobject.MessageType messageType) {
        return switch (messageType) {
            case CREATE -> MessageType.CREATE;
            case UPDATE -> MessageType.UPDATE;
            case DELETE -> MessageType.DELETE;
            case FAIL ->  MessageType.FAIL;
            case NO_DEF ->  MessageType.NO_DEF;
            case REJECT ->  MessageType.REJECT;
        };
    }

    private shop.shportfolio.common.domain.valueobject.MessageType toDomainMessageType(MessageType messageType) {
        return shop.shportfolio.common.domain.valueobject.MessageType.fromName(messageType.name());
    }

    private TransactionType toAvroTransactionType(shop.shportfolio.common.domain.valueobject.
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

    private shop.shportfolio.common.domain.valueobject.TransactionType toDomainTransactionType(TransactionType value) {
        switch (value) {
            case DEPOSIT:
                return shop.shportfolio.common.domain.valueobject.TransactionType.DEPOSIT;
            case WITHDRAWAL:
                return shop.shportfolio.common.domain.valueobject.TransactionType.WITHDRAWAL;
            case TRADE_BUY:
                return shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_BUY;
            case TRADE_SELL:
                return shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_SELL;
        }
        throw new IllegalArgumentException("Invalid transaction type");
    }
}
