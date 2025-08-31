package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.common.domain.valueobject.TradeId;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TradeDomainServiceImpl implements TradeDomainService {

    @Override
    public TradeCreatedEvent createTrade(TradeId tradeId, MarketId marketId, UserId userId, OrderId orderId, OrderPrice orderPrice,
                                         Quantity quantity, TransactionType transactionType, FeeAmount feeAmount
            , FeeRate feeRate) {
        Trade trade = Trade.createTrade(tradeId, marketId,userId, orderId,
                orderPrice, quantity, transactionType, feeAmount, feeRate);
        return new TradeCreatedEvent(trade, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }
    @Override
    public void applyExecutedTrade(OrderBook orderBook, Trade trade) {
        orderBook.applyExecutedTrade(trade);
    }


}
