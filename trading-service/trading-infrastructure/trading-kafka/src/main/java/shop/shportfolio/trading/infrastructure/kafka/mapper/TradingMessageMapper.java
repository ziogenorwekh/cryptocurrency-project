package shop.shportfolio.trading.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.*;
import shop.shportfolio.common.avro.AssetCode;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.dto.userbalance.UserBalanceKafkaResponse;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class TradingMessageMapper {


    public PredicatedTradeKafkaResponse toPredicatedTradeKafkaResponse(PredicatedTradeAvroModel model) {
        return new PredicatedTradeKafkaResponse(
                model.getTradeId(),
                model.getUserId(),
                model.getMarketId(),
                model.getBuyOrderId(),
                model.getSellOrderId(),
                model.getOrderPrice(),
                model.getQuantity(),
                model.getCreatedAt(),
                avroToDomainTransactionType(model.getTransactionType()),
                avroMessageTypeToAvroMessageType(model.getMessageType()),
                avroToDomainOrderType(model.getBuyOrderType()),
                avroToDomainOrderType(model.getSellOrderType())
        );
    }

    public CouponKafkaResponse couponResponseToCouponAvroModel(CouponAvroModel couponAvroModel) {
        return CouponKafkaResponse.builder()
                .couponId(new CouponId(UUID.fromString(couponAvroModel.getCouponId())))
                .owner(new UserId(UUID.fromString(couponAvroModel.getOwner())))
                .feeDiscount(new FeeDiscount((int) couponAvroModel.getFeeDiscount()))
                .issuedAt(new IssuedAt(couponAvroModel.getIssuedAt()))
                .expiryDate(new UsageExpiryDate(couponAvroModel.getExpiryDate()))
                .build();
    }

    public DepositWithdrawalKafkaResponse depositWithdrawalAvroModelToDepositWithdrawalKafkaResponse(
            DepositWithdrawalAvroModel model) {
        return new DepositWithdrawalKafkaResponse(UUID.fromString(model.getUserId()), model.getAmount(),
                avroTranscationTypetoDomainTransactionType(model.getTransactionType()),
                model.getTransactionTime(),
                avroMessageTypeToAvroMessageType(model.getMessageType()));
    }

    public TradeAvroModel tradeToTradeAvroModel(Trade trade, MessageType messageType) {

        ZonedDateTime zonedDateTime = trade.getCreatedAt().getValue().atOffset(ZoneOffset.UTC).toZonedDateTime();
        return TradeAvroModel.newBuilder()
                .setTradeId(trade.getId().getValue().toString())
                .setUserId(trade.getUserId().getValue().toString())
                .setBuyOrderId(trade.getBuyOrderId().getValue())
                .setSellOrderId(trade.getSellOrderId().getValue())
                .setOrderPrice(trade.getOrderPrice().getValue().doubleValue())
                .setQuantity(trade.getQuantity().getValue().doubleValue())
                .setTransactionType(domainToAvroTransactionType(trade.getTransactionType()))
                .setCreatedAt(zonedDateTime.toInstant())
                .setMessageType(domainMessageTypeToAvroMessageType(messageType))
                .setMarketId(trade.getMarketId().getValue())
                .build();
    }

    public UserBalanceAvroModel userBalanceToUserBalanceAvroModel(UserBalance userBalance, MessageType messageType) {
        AssetCode assetCode = switch (userBalance.getAssetCode()) {
            case KRW -> AssetCode.KRW;
        };
        long totalBalance = userBalance.getAvailableMoney().getValue().add(
                BigDecimal.valueOf(
                        userBalance.getLockBalances().stream().mapToLong(lockBalance ->
                                lockBalance.getLockedAmount().getValue().longValue()).sum())).longValue();
        return UserBalanceAvroModel.newBuilder()
                .setUserId(userBalance.getUserId().getValue().toString())
                .setAssetCode(assetCode)
                .setMessageType(domainMessageTypeToAvroMessageType(messageType))
                .setTotalBalance(totalBalance)
                .build();
    }

    private shop.shportfolio.common.avro.MessageType
    domainMessageTypeToAvroMessageType(MessageType type) {
        return switch (type) {
            case CREATE -> shop.shportfolio.common.avro.MessageType.CREATE;
            case DELETE -> shop.shportfolio.common.avro.MessageType.DELETE;
            case FAIL -> shop.shportfolio.common.avro.MessageType.FAIL;
            case REJECT -> shop.shportfolio.common.avro.MessageType.REJECT;
            case UPDATE -> shop.shportfolio.common.avro.MessageType.UPDATE;
            case NO_DEF -> shop.shportfolio.common.avro.MessageType.NO_DEF;
        };
    }

    private shop.shportfolio.common.domain.valueobject.TransactionType
    avroTranscationTypetoDomainTransactionType(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> shop.shportfolio.common.domain.valueobject.TransactionType.DEPOSIT;
            case WITHDRAWAL -> shop.shportfolio.common.domain.valueobject.TransactionType.WITHDRAWAL;
            case TRADE_BUY -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_BUY;
            case TRADE_SELL -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_SELL;
        };
    }

    private MessageType avroMessageTypeToAvroMessageType(
            shop.shportfolio.common.avro.MessageType type) {
        return switch (type) {
            case CREATE -> MessageType.CREATE;
            case DELETE -> MessageType.DELETE;
            case FAIL -> MessageType.FAIL;
            case REJECT -> MessageType.REJECT;
            case UPDATE -> MessageType.UPDATE;
            case NO_DEF -> MessageType.NO_DEF;
        };
    }

    private TransactionType domainToAvroTransactionType(shop.shportfolio.common.domain.valueobject.TransactionType type) {
        return switch (type) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAWAL -> TransactionType.WITHDRAWAL;
            case TRADE_BUY -> TransactionType.TRADE_BUY;
            case TRADE_SELL -> TransactionType.TRADE_SELL;
        };
    }

    private shop.shportfolio.common.domain.valueobject.TransactionType avroToDomainTransactionType(TransactionType type) {
        return switch (type) {
            case TRADE_SELL -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_SELL;
            case TRADE_BUY -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_BUY;
            case DEPOSIT -> shop.shportfolio.common.domain.valueobject.TransactionType.DEPOSIT;
            case WITHDRAWAL -> shop.shportfolio.common.domain.valueobject.TransactionType.WITHDRAWAL;
        };
    }

    private OrderType avroToDomainOrderType(shop.shportfolio.common.avro.OrderType orderType) {
        return switch (orderType) {
            case RESERVATION -> OrderType.RESERVATION;
            case MARKET -> OrderType.MARKET;
            case LIMIT -> OrderType.LIMIT;
        };
    }
}
