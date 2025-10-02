package shop.shportfolio.portfolio.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.*;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.dto.DepositWithdrawalKafkaResponse;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.event.CryptoUpdatedEvent;
import shop.shportfolio.portfolio.domain.view.CryptoView;

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
                .setTransactionId(depositWithdrawal.getId().getValue().toString())
                .setAmount(depositWithdrawal.getAmount().getValue())
                .setMessageType(MessageType.CREATE)
                .setTransactionTime(instant)
                .setUserId(depositWithdrawal.getUserId().getValue().toString())
                .setTransactionType(toAvroTransactionType(depositWithdrawal.getTransactionType()))
                .build();
    }

    public DepositWithdrawalKafkaResponse depositWithdrawalToDepositWithdrawalKafkaResponse(DepositWithdrawalAvroModel model) {
        return new DepositWithdrawalKafkaResponse(model.getTransactionId(),
                UUID.fromString(model.getUserId()),
                toDomainTransactionType(model.getTransactionType()),
                model.getAmount().longValue(),
                model.getTransactionTime().atZone(ZoneOffset.UTC).toLocalDateTime());
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
        return BalanceKafkaResponse.builder()
                .amount(model.getAmount())
                .assetCode(toDomainAssetCode(model.getAssetCode()))
                .userId(UUID.fromString(model.getUserId()))
                .messageType(toDomainMessageType(model.getMessageType()))
                .direction(toDomainDirectionType(model.getDirectionType()))
                .build();
    }

    public CryptoAvroModel toCryptoAvroModel(CryptoUpdatedEvent cryptoEvent) {
        CryptoView cryptoView = cryptoEvent.getDomainType();

        return CryptoAvroModel.newBuilder()
                .setBalanceId(cryptoView.getId().getValue().toString())
                .setUserId(cryptoView.getUserId().getValue().toString())
                .setMarketId(cryptoView.getMarketId().getValue())
                .setQuantity(cryptoView.getQuantity().getValue().toString())
                .setPurchasePrice(cryptoView.getPurchasePrice().getValue().toString())
                .setMessageType(toAvroMessageType(cryptoEvent.getMessageType()))
                .build();
    }

    private shop.shportfolio.common.domain.valueobject.DirectionType toDomainDirectionType(
            DirectionType directionType) {
        return switch (directionType) {
            case ADD ->  shop.shportfolio.common.domain.valueobject.DirectionType.ADD;
            case SUB ->   shop.shportfolio.common.domain.valueobject.DirectionType.SUB;
        };
    }

    private AssetCode toDomainAssetCode(shop.shportfolio.common.avro.AssetCode assetCode) {
        return switch (assetCode) {
            case KRW ->   shop.shportfolio.common.domain.valueobject.AssetCode.KRW;
        };
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
