package shop.shportfolio.trading.application.test.helper;


import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public class TestMapper {

    public PredicatedTradeKafkaResponse createKafkaResponseFromMatchedOrder(LimitOrder buyOrder, LimitOrder sellOrder,
                                                                             BigDecimal matchedPrice, BigDecimal matchedQuantity) {
        return PredicatedTradeKafkaResponse.builder()
                .tradeId(UUID.randomUUID().toString())
                .userId(buyOrder.getUserId().getValue().toString())
                .marketId(buyOrder.getMarketId().getValue())
                .buyOrderId(buyOrder.getId().getValue().toString())
                .sellOrderId(sellOrder.getId().getValue().toString())
                .orderPrice(matchedPrice.toPlainString())
                .quantity(matchedQuantity.toPlainString())
                .transactionType(TransactionType.TRADE_BUY)
                .messageType(MessageType.UPDATE)
                .createdAt(Instant.now())
                .build();
    }

    public PredicatedTradeKafkaResponse reservationOrderToPredicatedTradeKafkaResponse(Trade trade,
                                                                                       MessageType messageType,
                                                                                       OrderType buyOrderType,
                                                                                       OrderType sellOrderType) {
        return PredicatedTradeKafkaResponse.builder()
                .tradeId(trade.getId().toString())
                .userId(trade.getUserId().getValue().toString())
                .buyOrderId(trade.getBuyOrderId().getValue())
                .sellOrderId(trade.getSellOrderId().getValue())
                .marketId(trade.getMarketId().getValue())
                .orderPrice(trade.getOrderPrice().getValue().toPlainString())
                .quantity(trade.getQuantity().getValue().toPlainString())
                .createdAt(trade.getCreatedAt().getValue().toInstant(ZoneOffset.UTC))
                .transactionType(trade.getTransactionType())
                .messageType(messageType)
                .buyOrderType(buyOrderType)
                .sellOrderType(sellOrderType)
                .build();
    }
    public PredicatedTradeKafkaResponse marketOrderToPredicatedTradeKafkaResponse(MarketOrder marketOrder) {
//        return PredicatedTradeKafkaResponse.builder()
//                .tradeId()
//                .userId()
//                .buyOrderId()
//                .sellOrderId()
//                .orderPrice()
//                .quantity()
//                .createdAt()
//                .transactionType()
//                .messageType()
//                .build();
        return null;
    }
}
