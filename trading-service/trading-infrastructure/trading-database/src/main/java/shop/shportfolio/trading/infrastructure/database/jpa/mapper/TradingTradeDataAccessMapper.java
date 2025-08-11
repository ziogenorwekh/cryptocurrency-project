package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.valueobject.TradeId;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.TradeEntity;

@Component
public class TradingTradeDataAccessMapper {


    public TradeEntity tradeToTradeEntity(Trade trade) {
        return TradeEntity.builder()
                .tradeId(trade.getId().getValue())
                .marketId(trade.getMarketId().getValue())
                .userId(trade.getUserId().getValue())
                .buyOrderId(trade.getBuyOrderId().getValue())
                .sellOrderId(trade.getSellOrderId().getValue())
                .orderPrice(trade.getOrderPrice().getValue())
                .quantity(trade.getQuantity().getValue())
                .createdAt(trade.getCreatedAt().getValue())
                .transactionType(trade.getTransactionType())
                .feeRate(trade.getFeeRate().getRate())
                .feeAmount(trade.getFeeAmount().getValue())
                .build();
    }

    public Trade tradeEntityToTrade(TradeEntity trade) {
        return Trade.builder()
                .tradeId(new TradeId(trade.getTradeId()))
                .marketId(new MarketId(trade.getMarketId()))
                .userId(new UserId(trade.getUserId()))
                .buyOrderId(new OrderId(trade.getBuyOrderId()))
                .sellOrderId(new OrderId(trade.getSellOrderId()))
                .orderPrice(new OrderPrice(trade.getOrderPrice()))
                .quantity(new Quantity(trade.getQuantity()))
                .createdAt(new CreatedAt(trade.getCreatedAt()))
                .transactionType(trade.getTransactionType())
                .feeRate(new FeeRate(trade.getFeeRate()))
                .feeAmount(new FeeAmount(trade.getFeeAmount()))
                .build();
    }
}
