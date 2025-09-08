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
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class TradingMessageMapper {

    public LimitOrderAvroModel toLimitOrderAvroModel(LimitOrderCreatedEvent limitOrderCreatedEvent) {
        LimitOrder limitOrder = limitOrderCreatedEvent.getDomainType();
        ZonedDateTime zonedDateTime = limitOrder.getCreatedAt().getValue().atOffset(ZoneOffset.UTC).toZonedDateTime();
        return LimitOrderAvroModel
                .newBuilder()
                .setOrderId(limitOrder.getId().getValue())
                .setMarketId(limitOrder.getMarketId().getValue())
                .setUserId(limitOrder.getUserId().getValue().toString())
                .setOrderSide(domainToAvroOrderSide(limitOrder.getOrderSide()))
                .setOrderPrice(limitOrder.getOrderPrice().getValue().toString())
                .setQuantity(limitOrder.getQuantity().getValue().toString())
                .setRemainingQuantity(limitOrder.getRemainingQuantity().getValue().toString())
                .setOrderType(domainToAvroOrderType(limitOrder.getOrderType()))
                .setCreatedAt(zonedDateTime.toInstant())
                .setOrderStatus(domainToAvroOrderStatus(limitOrder.getOrderStatus()))
                .setMessageType(domainToAvroMessageType(limitOrderCreatedEvent.getMessageType()))
                .build();
    }

    public MarketOrderAvroModel toMarketOrderAvroModel(MarketOrderCreatedEvent marketOrderCreatedEvent) {
        MarketOrder marketOrder = marketOrderCreatedEvent.getDomainType();
        ZonedDateTime zonedDateTime = marketOrder.getCreatedAt().getValue().atOffset(ZoneOffset.UTC).toZonedDateTime();
        if (marketOrder.isBuyOrder()) {
            return MarketOrderAvroModel.newBuilder()
                    .setOrderId(marketOrder.getId().getValue())
                    .setMarketId(marketOrder.getMarketId().getValue())
                    .setUserId(marketOrder.getUserId().getValue().toString())
                    .setOrderSide(domainToAvroOrderSide(marketOrder.getOrderSide()))
                    .setOrderPrice(marketOrder.getOrderPrice().getValue().toString())
                    .setRemainingPrice(marketOrder.getRemainingPrice().getValue().toString())
                    .setOrderType(domainToAvroOrderType(marketOrder.getOrderType()))
                    .setCreatedAt(zonedDateTime.toInstant())
                    .setOrderStatus(domainToAvroOrderStatus(marketOrder.getOrderStatus()))
                    .setMessageType(domainToAvroMessageType(marketOrderCreatedEvent.getMessageType()))
                    .build();
        } else {
            return MarketOrderAvroModel.newBuilder()
                    .setOrderId(marketOrder.getId().getValue())
                    .setMarketId(marketOrder.getMarketId().getValue())
                    .setUserId(marketOrder.getUserId().getValue().toString())
                    .setOrderSide(domainToAvroOrderSide(marketOrder.getOrderSide()))
                    .setQuantity(marketOrder.getQuantity().getValue().toString())
                    .setRemainingQuantity(marketOrder.getRemainingQuantity().getValue().toString())
                    .setOrderType(domainToAvroOrderType(marketOrder.getOrderType()))
                    .setCreatedAt(zonedDateTime.toInstant())
                    .setOrderStatus(domainToAvroOrderStatus(marketOrder.getOrderStatus()))
                    .setMessageType(domainToAvroMessageType(marketOrderCreatedEvent.getMessageType()))
                    .build();
        }
    }

    public ReservationOrderAvroModel toReservationOrderAvroModel(ReservationOrderCreatedEvent reservationOrderCreatedEvent) {
        ReservationOrder reservationOrder = reservationOrderCreatedEvent.getDomainType();
        ZonedDateTime zonedDateTime = reservationOrder.getCreatedAt().getValue()
                .atOffset(ZoneOffset.UTC).toZonedDateTime();
        TriggerCondition triggerCondition = TriggerCondition.newBuilder().setTargetPrice(reservationOrder
                        .getTriggerCondition().getTargetPrice().getValue().toString())
                .setTriggerType(domainToAvroTriggerType(reservationOrder.getTriggerCondition().getValue())).build();
        return ReservationOrderAvroModel.newBuilder()
                .setOrderId(reservationOrder.getId().getValue())
                .setMarketId(reservationOrder.getMarketId().getValue())
                .setUserId(reservationOrder.getUserId().getValue().toString())
                .setOrderSide(domainToAvroOrderSide(reservationOrder.getOrderSide()))
                .setQuantity(reservationOrder.getQuantity().getValue().toString())
                .setRemainingQuantity(reservationOrder.getRemainingQuantity().getValue().toString())
                .setOrderType(domainToAvroOrderType(reservationOrder.getOrderType()))
                .setCreatedAt(zonedDateTime.toInstant())
                .setOrderStatus(domainToAvroOrderStatus(reservationOrder.getOrderStatus()))
                .setScheduledTime(reservationOrder.getScheduledTime().getValue().toInstant(ZoneOffset.UTC))
                .setTriggerCondition(triggerCondition)
                .setExpireAt(reservationOrder.getExpireAt().getValue().toInstant(ZoneOffset.UTC))
                .setIsRepeatable(reservationOrder.getIsRepeatable().getValue())
                .setMessageType(domainToAvroMessageType(reservationOrderCreatedEvent.getMessageType()))
                .build();
    }

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
                avroToDomainMessageType(model.getMessageType()),
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
                domaintoAvroTransactionType(model.getTransactionType()),
                model.getTransactionTime(),
                avroToDomainMessageType(model.getMessageType()));
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
                .setMessageType(domainToAvroMessageType(messageType))
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
                .setMessageType(domainToAvroMessageType(messageType))
                .setTotalBalance(totalBalance)
                .build();
    }

    private shop.shportfolio.common.avro.MessageType
    domainToAvroMessageType(MessageType type) {
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
    domaintoAvroTransactionType(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> shop.shportfolio.common.domain.valueobject.TransactionType.DEPOSIT;
            case WITHDRAWAL -> shop.shportfolio.common.domain.valueobject.TransactionType.WITHDRAWAL;
            case TRADE_BUY -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_BUY;
            case TRADE_SELL -> shop.shportfolio.common.domain.valueobject.TransactionType.TRADE_SELL;
        };
    }

    private MessageType avroToDomainMessageType(
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

    private shop.shportfolio.common.avro.OrderType domainToAvroOrderType(OrderType orderType) {
        return switch (orderType) {
            case RESERVATION -> shop.shportfolio.common.avro.OrderType.RESERVATION;
            case MARKET -> shop.shportfolio.common.avro.OrderType.MARKET;
            case LIMIT -> shop.shportfolio.common.avro.OrderType.LIMIT;
        };
    }

    private OrderSide domainToAvroOrderSide(shop.shportfolio.trading.domain.valueobject.OrderSide orderSide) {
        if (orderSide.isBuy()) return OrderSide.BUY;
        else return OrderSide.SELL;
    }

    private OrderStatus domainToAvroOrderStatus(shop.shportfolio.trading.domain.valueobject.OrderStatus orderStatus) {
        return switch (orderStatus) {
            case OPEN ->  OrderStatus.OPEN;
            case PARTIALLY_FILLED -> OrderStatus.PARTIALLY_FILLED;
            case FILLED -> OrderStatus.FILLED;
            case CANCELED -> OrderStatus.CANCELED;
        };
    }

    private TriggerType domainToAvroTriggerType(shop.shportfolio.trading.domain.valueobject.TriggerType triggerType) {
        return switch (triggerType) {
            case ABOVE ->  TriggerType.ABOVE;
            case BELOW -> TriggerType.BELOW;
        };
    }
}
