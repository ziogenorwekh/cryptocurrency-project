package shop.shportfolio.matching.infrastructure.kafka.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.avro.PredictedTradeAvroModel;
import shop.shportfolio.common.avro.TransactionType;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.matching.domain.entity.PredictedTrade;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class MatchingMessageMapper {

    public PredictedTradeAvroModel predictedTradeToPredictedTradeAvroModel(PredictedTradeCreatedEvent predictedTradeCreatedEvent) {

        PredictedTrade predictedTrade = predictedTradeCreatedEvent.getDomainType();
        TransactionType avroTxType = switch (predictedTrade.getTransactionType()) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAWAL -> TransactionType.WITHDRAWAL;
            case TRADE_BUY -> TransactionType.TRADE_BUY;
            case TRADE_SELL -> TransactionType.TRADE_SELL;
        };
        ZonedDateTime zonedDateTime = predictedTrade.getCreatedAt().getValue().atOffset(ZoneOffset.UTC).toZonedDateTime();
        PredictedTradeAvroModel predictedTradeAvroModel = PredictedTradeAvroModel.newBuilder()
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
                .build();
        return predictedTradeAvroModel;
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


}
