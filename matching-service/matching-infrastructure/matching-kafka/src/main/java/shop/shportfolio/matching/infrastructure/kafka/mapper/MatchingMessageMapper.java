package shop.shportfolio.matching.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.*;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.matching.domain.entity.PredictedTrade;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TriggerCondition;
import shop.shportfolio.trading.domain.valueobject.TriggerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class MatchingMessageMapper {

    public PredicatedTradeAvroModel predictedTradeToPredictedTradeAvroModel(PredictedTradeCreatedEvent predictedTradeCreatedEvent) {

        PredictedTrade predictedTrade = predictedTradeCreatedEvent.getDomainType();
        TransactionType avroTxType = switch (predictedTrade.getTransactionType()) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAWAL -> TransactionType.WITHDRAWAL;
            case TRADE_BUY -> TransactionType.TRADE_BUY;
            case TRADE_SELL -> TransactionType.TRADE_SELL;
        };
        ZonedDateTime zonedDateTime = predictedTrade.getCreatedAt().getValue().atOffset(ZoneOffset.UTC).toZonedDateTime();
        return PredicatedTradeAvroModel.newBuilder()
                .setTradeId(predictedTrade.getId().getValue().toString())
                .setUserId(predictedTrade.getUserId().getValue().toString())
                .setMarketId(predictedTrade.getMarketId().getValue())
                .setSellOrderId(predictedTrade.getSellOrderId().getValue())
                .setBuyOrderId(predictedTrade.getBuyOrderId().getValue())
                .setOrderPrice(predictedTrade.getOrderPrice().getValue().toString())
                .setQuantity(predictedTrade.getQuantity().getValue().toString())
                .setTransactionType(avroTxType)
                .setMessageType(domainMessageTypeToAvroMessageType(predictedTradeCreatedEvent.getMessageType()))
                .setCreatedAt(zonedDateTime.toInstant())
                .setBuyOrderType(domainOrderTypeToAvroOrderType(predictedTrade.getBuyOrderType()))
                .setSellOrderType(domainOrderTypeToAvroOrderType(predictedTrade.getSellOrderType()))
                .build();
    }

    public LimitOrder limitOrderToLimitOrderAvroModel(LimitOrderAvroModel limitOrderAvroModel) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(limitOrderAvroModel.getCreatedAt(), ZoneOffset.UTC);
        return LimitOrder.builder()
                .orderId(new OrderId(limitOrderAvroModel.getOrderId()))
                .marketId(new MarketId(limitOrderAvroModel.getMarketId()))
                .userId(new UserId(UUID.fromString(limitOrderAvroModel.getUserId())))
                .orderPrice(new OrderPrice(new BigDecimal(limitOrderAvroModel.getOrderPrice())))
                .orderSide(avroOrderSideToDomainOrderSide(limitOrderAvroModel.getOrderSide()))
                .orderStatus(avroOrderStatusToDomainOrderStatus(limitOrderAvroModel.getOrderStatus()))
                .orderType(avroOrderTypeToDomainOrderType(limitOrderAvroModel.getOrderType()))
                .quantity(new Quantity(new BigDecimal(limitOrderAvroModel.getQuantity())))
                .remainingQuantity(new Quantity(new BigDecimal(limitOrderAvroModel.getRemainingQuantity())))
                .createdAt(new CreatedAt(createdAt))
                .build();
    }

    public MarketOrder marketOrderToMarketOrderAvroModel(MarketOrderAvroModel marketOrderAvroModel) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(marketOrderAvroModel.getCreatedAt(), ZoneOffset.UTC);
        return MarketOrder.builder()
                .orderId(new OrderId(marketOrderAvroModel.getOrderId()))
                .marketId(new MarketId(marketOrderAvroModel.getMarketId()))
                .userId(new UserId(UUID.fromString(marketOrderAvroModel.getUserId())))
                .orderSide(avroOrderSideToDomainOrderSide(marketOrderAvroModel.getOrderSide()))
                .quantity(new Quantity(new BigDecimal(marketOrderAvroModel.getQuantity())))
                .remainingQuantity(new Quantity(new BigDecimal(marketOrderAvroModel.getRemainingQuantity())))
                .remainingPrice(new OrderPrice(new BigDecimal(marketOrderAvroModel.getRemainingPrice())))
                .orderType(avroOrderTypeToDomainOrderType(marketOrderAvroModel.getOrderType()))
                .createdAt(new CreatedAt(createdAt))
                .orderStatus(avroOrderStatusToDomainOrderStatus(marketOrderAvroModel.getOrderStatus()))
                .build();
    }

    public ReservationOrder reservationOrderToReservationOrderAvroModel(ReservationOrderAvroModel model) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(model.getCreatedAt(), ZoneOffset.UTC);
        LocalDateTime expiredAt = LocalDateTime.ofInstant(model.getExpireAt(), ZoneOffset.UTC);
        LocalDateTime scheduledTime = LocalDateTime.ofInstant(model.getScheduledTime(), ZoneOffset.UTC);
        return ReservationOrder.builder()
                .orderId(new OrderId(model.getOrderId()))
                .marketId(new MarketId(model.getMarketId()))
                .userId(new UserId(UUID.fromString(model.getUserId())))
                .orderSide(avroOrderSideToDomainOrderSide(model.getOrderSide()))
                .quantity(new Quantity(new BigDecimal(model.getQuantity())))
                .remainingQuantity(new Quantity(new BigDecimal(model.getRemainingQuantity())))
                .orderPrice(new  OrderPrice(new BigDecimal(model.getOrderPrice())))
                .orderType(avroOrderTypeToDomainOrderType(model.getOrderType()))
                .createdAt(new CreatedAt(createdAt))
                .orderStatus(avroOrderStatusToDomainOrderStatus(model.getOrderStatus()))
                .triggerCondition(new TriggerCondition(avroTriggerTypeToDomainTriggerType(model.getTriggerCondition().getTriggerType()),
                        new OrderPrice(new BigDecimal(model.getTriggerCondition().getTargetPrice()))))
                .scheduledTime(new ScheduledTime(scheduledTime))
                .expireAt(new ExpireAt(expiredAt))
                .isRepeatable(new IsRepeatable(model.getIsRepeatable()))
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

    private TriggerType avroTriggerTypeToDomainTriggerType(shop.shportfolio.common.avro.TriggerType type) {
        return switch (type) {
            case ABOVE ->  TriggerType.ABOVE;
            case BELOW ->  TriggerType.BELOW;
        };
    }

    private OrderSide avroOrderSideToDomainOrderSide(shop.shportfolio.common.avro.OrderSide orderSide) {
        return switch (orderSide) {
            case BUY -> OrderSide.BUY;
            case SELL -> OrderSide.SELL;
        };
    }

    private OrderStatus avroOrderStatusToDomainOrderStatus(shop.shportfolio.common.avro.OrderStatus orderStatus) {
        return switch (orderStatus) {
            case OPEN -> OrderStatus.OPEN;
            case PARTIALLY_FILLED -> OrderStatus.PARTIALLY_FILLED;
            case FILLED -> OrderStatus.FILLED;
            case CANCELED -> OrderStatus.CANCELED;
        };
    }

    private OrderType avroOrderTypeToDomainOrderType(shop.shportfolio.common.avro.OrderType orderType) {
        return switch (orderType) {
            case RESERVATION -> OrderType.RESERVATION;
            case LIMIT -> OrderType.LIMIT;
            case MARKET -> OrderType.MARKET;
        };
    }

    private shop.shportfolio.common.avro.OrderType domainOrderTypeToAvroOrderType(OrderType orderType) {
        return switch (orderType) {
            case RESERVATION -> shop.shportfolio.common.avro.OrderType.RESERVATION;
            case LIMIT -> shop.shportfolio.common.avro.OrderType.LIMIT;
            case MARKET -> shop.shportfolio.common.avro.OrderType.MARKET;
        };
    }
}
